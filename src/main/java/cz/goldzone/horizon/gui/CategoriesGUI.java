package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class CategoriesGUI implements IGUI {

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 36, "Choose a category to view player warps");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        InteractiveItem farmItem = new InteractiveItem(Category.FARM.getMaterial(), Category.FARM.getSlot(), Category.FARM.getDisplayName());
        farmItem.setLore(
                " ",
                "<gray>Click to view player warps in this category",
                " "
        );
        farmItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.FARM).getInventory()));
        inv.setItem(farmItem.getSlot(), farmItem);

        InteractiveItem shopItem = new InteractiveItem(Category.SHOP.getMaterial(), Category.SHOP.getSlot(), Category.SHOP.getDisplayName());
        shopItem.setLore(
                " ",
                "<gray>Click to view player warps in this category",
                " "
        );
        shopItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.SHOP).getInventory()));
        inv.setItem(shopItem.getSlot(), shopItem);

        InteractiveItem miscItem = new InteractiveItem(Category.MISC.getMaterial(), Category.MISC.getSlot(), Category.MISC.getDisplayName());
        miscItem.setLore(
                " ",
                "<gray>Click to view player warps in this category",
                " "
        );
        miscItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.MISC).getInventory()));
        inv.setItem(miscItem.getSlot(), miscItem);

        return inv;
    }
}