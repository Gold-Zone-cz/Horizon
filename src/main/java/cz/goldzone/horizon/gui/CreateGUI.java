package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CreateGUI implements IGUI {
    ConfigManager configManager = Main.getConfigManager();
    private final String warpName;

    public CreateGUI(String warpName) {
        this.warpName = warpName;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, "Click to select your warp category");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        for (Category category : Category.values()) {
            InteractiveItem mobItem = new InteractiveItem(Objects.requireNonNull(category.getMaterial()), category.getSlot(), category.name());
            mobItem.setLore(
                    "§r\n",
                    "§7Click to select your warp category" + category.getDisplayName() + "\n",
                    "§r"
            );
            mobItem.onClick((player, clickType) -> {
                configManager.getConfig("player_warps.yml").set(warpName + ".owner", player.getName());
                configManager.getConfig("player_warps.yml").set(warpName + ".playerlocation", player.getLocation());
                configManager.getConfig("player_warps.yml").set(warpName + ".category", category.name());
                configManager.saveConfig("player_warps.yml");

                player.sendMessage(Lang.getPrefix("PlayerWarps") + "§a");
                player.closeInventory();
            });
        }
        return inv;
    }
}
