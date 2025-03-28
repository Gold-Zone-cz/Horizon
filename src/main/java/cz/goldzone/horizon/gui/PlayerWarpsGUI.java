package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.ConfigManager;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PlayerWarpsGUI implements IGUI {
    private final Category category;
    private final ConfigManager configManager = Main.getConfigManager();

    public PlayerWarpsGUI(Category category) {
        this.category = category;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, ChatColor.stripColor(category.getDisplayName()));
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            FileConfiguration warpsConfig = configManager.getConfig("warps.yml");

            List<String> warps = warpsConfig.getKeys(false).stream()
                    .filter(key -> category.name().equalsIgnoreCase(warpsConfig.getString(key + ".category")))
                    .limit(28)
                    .toList();

            int slot = 10;

            for (String warp : warps) {
                InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()), slot);
                item.setSkullOwner(configManager.getConfig("player_warps.yml").getString(warp + ".owner"));
                item.setLore(
                        "§r\n",
                        "§7Click to teleport to this player warp\n",
                        "§r"
                );
                item.setDisplayName("§e" + warp);
                item.onClick((player, clickType) -> {
                    Location location = (Location) configManager.getConfig("player_warps.yml").get(warp + ".playerlocation");
                    if (location != null) {
                        player.teleport(location);
                        player.sendMessage("§fSuccessfully teleported to §a" + warp + "§a's §fwarp");
                    } else {
                        player.sendMessage("§cWarp location not found.");
                    }
                });

                inv.setItem(slot, item);

                if ((slot - 7) % 9 == 0) slot += 3;
                else slot++;
            }
        });

        return inv;
    }
}
