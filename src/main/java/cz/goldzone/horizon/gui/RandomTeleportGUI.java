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

import java.util.Objects;
import java.util.Random;

public class RandomTeleportGUI implements Listener, IGUI {
    private final Random random = new Random();
    private static final String WORLD_NAME = "world";
    private static final String NETHER_NAME = "world_nether";
    private static final String END_NAME = "world_the_end";
    private static final String TELEPORTING_MESSAGE = "Teleporting in 3 seconds...";
    private static final String TELEPORT_SUCCESS_MESSAGE = "Teleported to the %s!";
    private static final String WORLD_NOT_FOUND_MESSAGE = "%s world not found! Please contact an admin.";

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Random Teleport");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        createTeleportItem(inv, 11, XMaterial.GRASS_BLOCK, WORLD_NAME, "Free", "random location in the world");
        createTeleportItem(inv, 12, XMaterial.NETHERRACK, NETHER_NAME, "$10000", "random location in the Nether");
        createTeleportItem(inv, 13, XMaterial.END_STONE, END_NAME, "$80000", "random location in the End");

        return inv;
    }

    private void createTeleportItem(Inventory inv, int slot, XMaterial material, String worldName, String price, String locationDescription) {
        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(material.parseItem()));
        item.setDisplayName("<green><bold>" + worldName.substring(0, 1).toUpperCase() + worldName.substring(1)); // Capitalize first letter
        item.setLore(String.format("<gray>Price: <green>%s\n<gray>Click to teleport to a <green>%s <gray>in the %s\n", price, locationDescription, worldName));

        item.onClick((player, clickType) -> teleportPlayer(player, worldName));
        inv.setItem(slot, item);
    }

    private void teleportPlayer(Player player, String worldName) {
        if (WorldManager.isValidSourceWorld(player.getWorld())) {
            player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport from valid worlds.");
            return;
        }

        World targetWorld = Bukkit.getWorld(worldName);
        if (targetWorld == null || !WorldManager.isValidSourceWorld(targetWorld)) {
            player.sendMessage(Lang.getPrefix("RTP") + String.format(WORLD_NOT_FOUND_MESSAGE, worldName));
            return;
        }

        player.closeInventory();
        player.sendMessage(Lang.getPrefix("RTP") + TELEPORTING_MESSAGE);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location randomLocation = getRandomSafeLocation(targetWorld);
                player.teleport(Objects.requireNonNull(randomLocation));
                player.sendMessage(Lang.getPrefix("RTP") + String.format(TELEPORT_SUCCESS_MESSAGE, worldName));
            }
        }.runTaskLater(Main.getInstance(), 60L);
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
            if (y > 255) return null;
            if (y <= 0) return null;
        }
        return new Location(world, x, y + 1, z);
    }
}