package cz.goldzone.horizon.admin;

import cz.goldzone.horizon.Main;
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

import java.util.Objects;

public class PlayerScan implements Listener {

    private static final String EVENT_WORLD = "Event";
    private static final String STAFF_PERMISSION = "horizon.staff.scan";

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission(STAFF_PERMISSION)) return;
        scheduleScan(e.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (e.getPlayer().hasPermission(STAFF_PERMISSION)) return;
        if (e.getFrom().getName().equals(EVENT_WORLD) || Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName().equals(EVENT_WORLD))
            return;
        scan(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer().hasPermission(STAFF_PERMISSION)) return;
        if (Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName().equals(EVENT_WORLD)) return;
        scan((Player) e.getPlayer());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (p.hasPermission(STAFF_PERMISSION)) return;
        if (Objects.requireNonNull(p.getLocation().getWorld()).getName().equals(EVENT_WORLD)) return;
        scan(p);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission(STAFF_PERMISSION)) return;
        if (Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName().equals(EVENT_WORLD)) return;

        String[] args = e.getMessage().toLowerCase().split(" ");
        if (!args[0].equals("/trade") && !args[0].equals("/ah") && !args[0].startsWith("/tp")) return;
        scan(e.getPlayer());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (e.getPlayer().hasPermission(STAFF_PERMISSION)) return;
        if (Objects.requireNonNull(e.getPlayer().getLocation().getWorld()).getName().equals(EVENT_WORLD)) return;
        if (scan(e.getPlayer())) e.setCancelled(true);
    }

    private void scheduleScan(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                scan(p);
            }
        }.runTaskLater(Main.getInstance(), 20L);
    }

    private boolean scan(Player p) {
        boolean ban = false;

        if (p.hasPermission(STAFF_PERMISSION)) return false;

        ban = checkInventory(p, p.getInventory().getContents()) || checkInventory(p, p.getEnderChest().getContents());

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
                p.getEnderChest().remove(i);
                notifyStaff(p, i);
            }
        }

        return ban;
    }

    private void notifyStaff(Player p, ItemStack i) {
        for (Player pp : Bukkit.getOnlinePlayers()) {
            if (pp.hasPermission("horizon.staff.notify")) {
                pp.sendMessage(p.getName() + " v item " + i.getType());
            }
        }
    }

    private boolean isIllegalItem(ItemStack i) {
        if (i == null || i.getType() == Material.AIR) return false;

        Material type = i.getType();
        return type == Material.BEDROCK ||
                type == Material.BARRIER ||
                type == Material.COMMAND_BLOCK;
    }
}
