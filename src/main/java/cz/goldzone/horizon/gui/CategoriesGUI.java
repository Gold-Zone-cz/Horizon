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
            InteractiveItem categoryItem = createCategoryItem(category);
            categoryItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, category).getInventory()));
            inv.setItem(category.getSlot(), categoryItem);
        }

        return inv;
    }

    private InteractiveItem createCategoryItem(Category category) {
        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(category.getMaterial()), category.getSlot(), category.name());
        item.setLore(
                " ",
                "<gray>Click to view player warps in this category",
                " "
        );
        return item;
    }
}