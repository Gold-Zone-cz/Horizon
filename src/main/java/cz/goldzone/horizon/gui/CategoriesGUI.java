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
        Inventory inv = Bukkit.createInventory(this, 27, "Choose a category");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        InteractiveItem farmItem = new InteractiveItem(Category.FARMS.getMaterial(), Category.FARMS.getDisplayName());
        farmItem.setLore(
                " ",
                "<gray>Click to view player warps",
                " ",
                "<gray>Most popular warp - <yellow>" + Category.FARMS.getPopularPlayerWarp(),
                "<gray>Enjoy your farming!",
                " "
        );
        farmItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.FARMS).getInventory()));
        inv.setItem(11, farmItem);

        InteractiveItem shopItem = new InteractiveItem(Category.SHOPS.getMaterial(), Category.SHOPS.getDisplayName());
        shopItem.setLore(
                " ",
                "<gray>Click to view player warps",
                " ",
                "<gray>Most popular warp - <green>" + Category.SHOPS.getPopularPlayerWarp(),
                "<gray>Enjoy your shopping!",
                " "
        );
        shopItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.SHOPS).getInventory()));
        inv.setItem(13, shopItem);

        InteractiveItem miscItem = new InteractiveItem(Category.MISC.getMaterial(), Category.MISC.getDisplayName());
        miscItem.setLore(
                " ",
                "<gray>Click to view player warps",
                " ",
                "<gray>Most popular warp - <aqua>" + Category.MISC.getPopularPlayerWarp(),
                "<gray>Enjoy your activities!",
                " "
        );
        miscItem.onClick((player, clickType) -> player.openInventory(new PlayerWarpsGUI(player, Category.MISC).getInventory()));
        inv.setItem(15, miscItem);

        return inv;
    }
}