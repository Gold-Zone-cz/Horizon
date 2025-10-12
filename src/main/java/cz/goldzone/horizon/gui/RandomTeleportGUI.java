package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.BackCommandManager;
import cz.goldzone.horizon.managers.EconomyManager;
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

import java.util.*;

public class RandomTeleportGUI implements IGUI {

    private static final Map<String, String[]> VALID_TELEPORTATION = new HashMap<>();
    private final Random random = new Random();

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
        if ("Not available".equalsIgnoreCase(price)) {
            item.setLore("<gray>\n<gray>This world will unlock soon...\n<gray>");
        } else {
            item.setLore(Lang.format("<gray>Price: %{1}<gray>\n<gray>Click to teleport to a %{2}", color + price, color + "random location"));
            item.onClick((player, clickType) -> handleTeleportClick(player, worldName, price));
        }

        inv.setItem(slot, item);
    }

    private void handleTeleportClick(Player player, String worldName, String price) {
        if (!price.equalsIgnoreCase("Free") && !price.equalsIgnoreCase("Not available")) {
            double amount = parsePrice(price);
            double balance = EconomyManager.getBalance(player);

            if (balance < amount) {
                player.sendMessage(Lang.getPrefix("RTP") + Lang.format("<red>You don't have enough money! You need <gray>$%,.0f", String.valueOf(amount)));
                player.closeInventory();
                return;
            }

            EconomyManager.withdraw(player, amount);
            player.sendMessage(Lang.getPrefix("RTP") + Lang.format("<gray>You paid <red>$%,.0f <gray>for teleportation.", String.valueOf(amount)));
        }

        teleportPlayer(player, worldName);
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


    private void teleportPlayer(Player player, String worldName) {
        String currentWorld = player.getWorld().getName();

        if (!isValidWorld(currentWorld)) {
            player.sendMessage(Lang.getPrefix("RTP") + "<red>You cannot teleport from this world!");
            return;
        }

        if (isTeleportationBlocked(currentWorld, worldName)) {
            player.sendMessage(Lang.getPrefix("RTP") + "<red>You cannot teleport from the " + currentWorld + " to the " + worldName);
            return;
        }

        World targetWorld = Bukkit.getWorld(worldName);
        if (!WorldManager.isValidSourceWorld(targetWorld)) {
            player.sendMessage(Lang.getPrefix("RTP") + "<red>Target world not found!");
            return;
        }

        player.closeInventory();
        player.sendMessage(Lang.getPrefix("RTP") + "<gray>Teleporting in <red>3 <gray>seconds...");


        new BukkitRunnable() {
            @Override
            public void run() {
                Location randomLocation = getRandomSafeLocation(targetWorld);

                if (randomLocation == null) {
                    player.sendMessage(Lang.getPrefix("RTP") + "<red>Could not find a safe location.");
                    return;
                }

                BackCommandManager.setLastLocation(player, player.getLocation());
                player.teleport(randomLocation);
                player.sendMessage(Lang.getPrefix("RTP") + "<gray>Teleported to <green>" + worldName);
            }
        }.runTaskLater(Main.getInstance(), 60L);
    }

    private boolean isValidWorld(String worldName) {
        return VALID_TELEPORTATION.containsKey(worldName);
    }

    private boolean isTeleportationBlocked(String fromWorld, String toWorld) {
        return !VALID_TELEPORTATION.containsKey(fromWorld)
                || !Arrays.asList(VALID_TELEPORTATION.get(fromWorld)).contains(toWorld);
    }

    private Location getRandomSafeLocation(World world) {
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(10000) - 5000;
            int z = random.nextInt(10000) - 5000;
            int y = world.getHighestBlockYAt(x, z);

            Block block = world.getBlockAt(x, y - 1, z);
            if (block.getType().isSolid() && block.getType() != Material.LAVA && block.getType() != Material.WATER) {
                return new Location(world, x + 0.5, y, z + 0.5);
            }
        }
        return null;
    }
}