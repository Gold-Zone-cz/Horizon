package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.WorldManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.*;

public class RandomTeleportGUI implements Listener, IGUI {
    private final Random random = new Random();
    private static final Map<String, String[]> VALID_TELEPORTATION = new HashMap<>();

    static {
        VALID_TELEPORTATION.put("world", new String[]{"world_nether", "world_the_end"});
        VALID_TELEPORTATION.put("world_nether", new String[]{"world", "world_the_end"});
        VALID_TELEPORTATION.put("world_the_end", new String[]{"world", "world_nether"});
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Random Teleport");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        createTeleportItem(inv, 12, XMaterial.GRASS_BLOCK, "World", "world", "Free", "<green>");
        createTeleportItem(inv, 13, XMaterial.NETHERRACK, "Nether World", "world_nether", "$10000", "<red>");
        createTeleportItem(inv, 14, XMaterial.END_STONE, "The End", "world_the_end", "Not available", "<yellow>");

        return inv;
    }

    private void createTeleportItem(Inventory inv, int slot, XMaterial material, String name, String worldName, String price, String color) {
        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(material.parseItem()));

        item.setDisplayName(color + "<bold>" + name);
        if (material == XMaterial.END_STONE && "Not available".equals(price)) {
            item.setLore("<gray>\n<gray>This world will unlock soon...\n<gray>");
        } else {
            item.setLore(Lang.format("<gray>Price: %{1}<gray>\n<gray>Click to teleport to a %{2}", color + price, color + "random location"));
            item.onClick((player, clickType) -> teleportPlayer(player, worldName));
        }

        inv.setItem(slot, item);
    }

    private void teleportPlayer(Player player, String worldName) {
        String playerWorldName = player.getWorld().getName();

        if (!isValidWorld(playerWorldName)) {
            player.sendMessage(Lang.getPrefix("RTP") + "<red>You cannot teleport from this world!");
            return;
        }

        if (isTeleportationBlocked(playerWorldName, worldName)) {
            player.sendMessage(Lang.getPrefix("RTP") + "<red>You cannot teleport from the " + playerWorldName + " to the " + worldName + "!");
            return;
        }

        World targetWorld = Bukkit.getWorld(worldName);
        if (!WorldManager.isValidSourceWorld(targetWorld)) {
            player.sendMessage(Lang.getPrefix("RTP") + Lang.format("<red>%{1} world not found! Please contact an admin.", worldName));
            player.closeInventory();
            return;
        }

        player.closeInventory();
        player.sendMessage(Lang.getPrefix("RTP") + "<gray>Teleporting in <red>3 <gray>seconds...");
        new BukkitRunnable() {
            @Override
            public void run() {
                Location randomLocation = getRandomSafeLocation(targetWorld);

                if (randomLocation == null) {
                    player.sendMessage(Lang.getPrefix("RTP") + "<red>Could not find a safe location to teleport.");
                    return;
                }

                player.teleport(randomLocation);
                player.sendMessage(Lang.getPrefix("RTP") + Lang.format("<gray>Teleported to the <green>%{1}", worldName));
            }
        }.runTaskLater(Main.getInstance(), 60L);
    }

    private boolean isValidWorld(String worldName) {
        return VALID_TELEPORTATION.containsKey(worldName);
    }

    private boolean isTeleportationBlocked(String fromWorld, String toWorld) {
        return !fromWorld.equals(toWorld) && (!VALID_TELEPORTATION.containsKey(fromWorld) || !Arrays.asList(VALID_TELEPORTATION.get(fromWorld)).contains(toWorld));
    }

    private Location getRandomSafeLocation(World world) {
        int x = random.nextInt(10000) - 5000;
        int z = random.nextInt(10000) - 5000;
        int y;

        return switch (world.getEnvironment()) {
            case NORMAL -> findSafeLocation(world, x, world.getHighestBlockYAt(x, z), z);
            case NETHER -> {
                y = random.nextInt(128) + 64;
                yield findSafeLocation(world, x, y, z);
            }
            case THE_END -> {
                y = random.nextInt(50) + 50;
                yield findSafeLocation(world, x, y, z);
            }
            default -> null;
        };
    }

    private static Location findSafeLocation(World world, int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);
        while (!block.getType().isSolid() || block.getType() == Material.LAVA || block.getType() == Material.WATER) {
            y++;
            block = world.getBlockAt(x, y, z);
            if (y > 255 || y <= 0) return null;
        }
        return new Location(world, x, y + 1, z);
    }
}