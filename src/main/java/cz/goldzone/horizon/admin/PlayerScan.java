package cz.goldzone.horizon.admin;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerScan implements Listener {

    private static final String EVENT_WORLD = "Event";
    private static final long SCHEDULE_DELAY = 20L;

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        if (!hasScanPermission(e.getPlayer())) {
            scheduleScan(e.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (!hasScanPermission(e.getPlayer()) && !isInEventWorld(e.getPlayer())) {
            scan(e.getPlayer());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!hasScanPermission((Player) e.getPlayer()) && !isInEventWorld((Player) e.getPlayer())) {
            scan((Player) e.getPlayer());
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p && !hasScanPermission(p) && !isInEventWorld(p)) {
            scan(p);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!hasScanPermission(e.getPlayer()) && !isInEventWorld(e.getPlayer())) {
            String[] args = e.getMessage().toLowerCase().split(" ");
            if (args.length > 0 && ("/trade".equals(args[0]) || "/ah".equals(args[0]) || args[0].startsWith("/tp"))) {
                scan(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!hasScanPermission(e.getPlayer()) && !isInEventWorld(e.getPlayer()) && scan(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    private void scheduleScan(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                scan(p);
            }
        }.runTaskLater(Main.getInstance(), SCHEDULE_DELAY);
    }

    private boolean scan(Player p) {
        if (hasScanPermission(p)) return false;

        boolean ban = checkInventory(p, p.getInventory().getContents()) ||
                checkInventory(p, p.getEnderChest().getContents());

        if (ban) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban -s " + p.getName() + " 30d Unauthorised item(s) in inventory");
        }
        return ban;
    }

    private boolean checkInventory(Player p, ItemStack[] items) {
        boolean ban = false;

        for (ItemStack i : items) {
            if (isIllegalItem(i)) {
                ban = true;
                p.getInventory().remove(i);
                notifyStaff(p, i);
            }
        }

        return ban;
    }

    private void notifyStaff(Player p, ItemStack i) {
        for (Player pp : Bukkit.getOnlinePlayers()) {
            if (pp.hasPermission("horizon.staff.notify")) {
                pp.sendMessage(Lang.format("%{1} has an illegal item: %{2}", p.getName(), i.getType().toString()));
            }
        }
    }

    private boolean isIllegalItem(ItemStack i) {
        return i != null && i.getType() != Material.AIR &&
                (i.getType() == Material.BEDROCK || i.getType() == Material.BARRIER || i.getType() == Material.COMMAND_BLOCK);
    }

    private boolean hasScanPermission(Player player) {
        return player.hasPermission("horizon.staff.scan");
    }

    private boolean isInEventWorld(Player player) {
        return player.getLocation().getWorld() != null && player.getLocation().getWorld().getName().equals(EVENT_WORLD);
    }
}