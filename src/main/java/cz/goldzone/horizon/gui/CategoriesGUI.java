package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CategoriesGUI implements IGUI {

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, "Choose a category");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        for (Category category : Category.values()) {
            InteractiveItem mobItem = new InteractiveItem(Objects.requireNonNull(category.getMaterial()), category.getSlot(), category.name());
            mobItem.setLore(
                    "§r\n",
                    "§7Click to view warps in category" + category.getDisplayName() + "\n",
                    "§r"
            );
            mobItem.onClick((player, clickType) -> {
                player.openInventory(new PlayerWarpsGUI(category).getInventory());
            });
        }
        return inv;
    }
}