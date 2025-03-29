package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;


public class CreateGUI implements IGUI {
    private final String playerWarpName;

    public CreateGUI(String playerWarpName) {
        this.playerWarpName = playerWarpName;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Select category for " + playerWarpName);
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        InteractiveItem farmItem = new InteractiveItem(Category.FARM.getMaterial(), Category.FARM.getSlot());
        farmItem.setDisplayName(Category.FARM.getDisplayName());
        farmItem.setLore("<gray>Click to select this category");
        farmItem.onClick((player, clickType) -> selectCategory(player, Category.FARM));
        inv.setItem(farmItem.getSlot(), farmItem);

        InteractiveItem shopItem = new InteractiveItem(Category.SHOP.getMaterial(), Category.SHOP.getSlot());
        shopItem.setDisplayName(Category.SHOP.getDisplayName());
        shopItem.setLore("<gray>Click to select this category");
        shopItem.onClick((player, clickType) -> selectCategory(player, Category.SHOP));
        inv.setItem(shopItem.getSlot(), shopItem);

        InteractiveItem miscItem = new InteractiveItem(Category.MISC.getMaterial(), Category.MISC.getSlot());
        miscItem.setDisplayName(Category.MISC.getDisplayName());
        miscItem.setLore("<gray>Click to select this category");
        miscItem.onClick((player, clickType) -> selectCategory(player, Category.MISC));
        inv.setItem(miscItem.getSlot(), miscItem);

        return inv;
    }

    private void selectCategory(Player player, Category category) {
        PlayerWarpsManager.createPlayerWarp(player, playerWarpName, category);
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<green>Player warp created successfully!");
        player.closeInventory();
    }
}
