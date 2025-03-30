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

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Random Teleport");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        InteractiveItem worldItem = new InteractiveItem(Objects.requireNonNull(XMaterial.GRASS_BLOCK.parseItem()));
        worldItem.setDisplayName("<green><bold>World");
        worldItem.setLore("""
                
                <gray>Price: <green>Free
                <gray>Click to teleport to a <green>random location <gray>in the world
                
                """);
        worldItem.onClick((player, clickType) -> {
            if (WorldManager.isValidSourceWorld(player.getWorld())) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport from valid worlds.");
                return;
            }

            if (!WorldManager.isValidSourceWorld(Objects.requireNonNull(Bukkit.getWorld("world")))) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport to the world, the Nether, or the End.");
                return;
            }

            player.closeInventory();

            player.sendMessage(Lang.getPrefix("RTP") + "Teleporting in 3 seconds...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    World world = Bukkit.getWorld("world");
                    if (world == null) {
                        player.sendMessage(Lang.getPrefix("RTP") + "World not found! Please contact an admin.");
                        return;
                    }

                    Location randomLocation = getRandomSafeLocation(world);
                    player.teleport(randomLocation);
                    player.sendMessage(Lang.getPrefix("RTP") + "Teleported to the world!");
                }
            }.runTaskLater(Main.getInstance(), 60L);
            inv.setItem(11, XMaterial.GRASS_BLOCK.parseItem());
        });

        InteractiveItem netherItem = new InteractiveItem(Objects.requireNonNull(XMaterial.NETHERRACK.parseItem()));
        netherItem.setDisplayName("<red><bold>Nether");
        netherItem.setLore("""
                
                <gray>Price: <red>$10000
                <gray>Click to teleport to a <red>random location <gray>in the Nether
                
                """);
        netherItem.onClick((player, clickType) -> {
            if (WorldManager.isValidSourceWorld(player.getWorld())) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport from valid worlds.");
                return;
            }

            if (!WorldManager.IsValidRTPWorld(Objects.requireNonNull(Bukkit.getWorld("world_nether")))) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport to the world, the Nether, or the End.");
                return;
            }

            player.closeInventory();
            player.sendMessage(Lang.getPrefix("RTP") + "Teleporting in 3 seconds...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    World world = Bukkit.getWorld("world_nether");
                    if (world == null) {
                        player.sendMessage(Lang.getPrefix("RTP") + "Nether world not found! Please contact an admin.");
                        return;
                    }

                    Location randomLocation = getRandomSafeLocation(world);
                    player.teleport(randomLocation);
                    player.sendMessage(Lang.getPrefix("RTP") + "Teleported to the Nether!");
                }
            }.runTaskLater(Main.getInstance(), 60L);
            inv.setItem(12, XMaterial.NETHERRACK.parseItem());
        });

        InteractiveItem endItem = new InteractiveItem(Objects.requireNonNull(XMaterial.END_STONE.parseItem()));
        endItem.setDisplayName("<yellow><bold>End");
        endItem.setLore("""
                
                <gray>Price: <yellow>$80000
                <gray>Click to teleport to a <yellow>random location <gray>in the End

                """);
        endItem.onClick((player, clickType) -> {
            if (WorldManager.isValidSourceWorld(player.getWorld())) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport from valid worlds.");
                return;
            }

            if (!WorldManager.isValidSourceWorld(Objects.requireNonNull(Bukkit.getWorld("world_the_end")))) {
                player.sendMessage(Lang.getPrefix("RTP") + "You can only teleport to the world, the Nether, or the End.");
                return;
            }

            player.closeInventory();
            player.sendMessage(Lang.getPrefix("RTP") + "Teleporting in 3 seconds...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    World world = Bukkit.getWorld("world_the_end");
                    if (world == null) {
                        player.sendMessage(Lang.getPrefix("RTP") + "End world not found! Please contact an admin.");
                        return;
                    }

                    Location randomLocation = getRandomSafeLocation(world);
                    player.teleport(randomLocation);
                    player.sendMessage(Lang.getPrefix("RTP") + "Teleported to the End!");
                }
            }.runTaskLater(Main.getInstance(), 60L);
            inv.setItem(13, XMaterial.END_STONE.parseItem());
        });

        return inv;
    }

    private Location getRandomSafeLocation(World world) {
        int x = random.nextInt(10000) - 5000;
        int z = random.nextInt(10000) - 5000;

        if (world.getEnvironment() == World.Environment.NORMAL) {
            return findSafeLocation(world, x, world.getHighestBlockYAt(x, z), z);
        }

        if (world.getEnvironment() == World.Environment.NETHER) {
            int y = random.nextInt(128) + 64;
            return findSafeLocation(world, x, y, z);
        }

        if (world.getEnvironment() == World.Environment.THE_END) {
            int y = random.nextInt(50) + 50;
            return findSafeLocation(world, x, y, z);
        }

        return findSafeLocation(world, x, world.getHighestBlockYAt(x, z), z);
    }

    private static Location findSafeLocation(World world, int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);
        while (!block.getType().isSolid() || block.getType() == Material.LAVA || block.getType() == Material.WATER) {
            y++;
            block = world.getBlockAt(x, y, z);

            if (y > 255) break;
            if (y <= 0) {
                return null;
            }
        }
        return new Location(world, x, y + 1, z);
    }
}
