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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CreateGUI implements IGUI {
    private final String playerWarpName;

    public CreateGUI(String playerWarpName) {
        this.playerWarpName = playerWarpName;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, "Select Player Warp Category");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        for (Category category : Category.values()) {
            InteractiveItem categoryItem = createCategoryItem(category);
            categoryItem.onClick((player, clickType) -> selectCategory(player, category));
            inv.setItem(category.getSlot(), categoryItem);
        }

        return inv;
    }

    private InteractiveItem createCategoryItem(Category category) {
        ItemStack item = new ItemStack(Objects.requireNonNull(category.getMaterial()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("<yellow>" + category.getDisplayName());
            meta.setLore(List.of(
                    "",
                    "<gray>Click to select your warp category:",
                    "<gold>" + category.getDisplayName(),
                    ""
            ));
            item.setItemMeta(meta);
        }
        return new InteractiveItem(item, category.getSlot());
    }

    private void selectCategory(Player player, Category category) {
        PlayerWarpsManager.createPlayerWarp(player, playerWarpName, category);
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<green>Warp created successfully!");
        player.closeInventory();
    }
}
