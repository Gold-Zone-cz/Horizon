package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConfirmGUI implements IGUI {
    private final Runnable onConfirm;

    public ConfirmGUI(Runnable onConfirm) {
        this.onConfirm = Objects.requireNonNull(onConfirm, "onConfirm cannot be null");
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Are you sure?");

        int[] confirmSlots = {0, 9, 18, 1, 10, 19, 2, 11, 20};
        int[] emptySlots = {3, 12, 21, 4, 13, 22, 5, 14, 23};
        int[] cancelSlots = {6, 15, 24, 7, 16, 25, 8, 17, 26};

        createInteractiveItems(inv, confirmSlots, XMaterial.LIME_STAINED_GLASS_PANE, "<green>Confirm", player -> {
            player.closeInventory();
            onConfirm.run();
        });

        createInteractiveItems(inv, emptySlots, XMaterial.GRAY_STAINED_GLASS_PANE, " ", null);

        createInteractiveItems(inv, cancelSlots, XMaterial.RED_STAINED_GLASS_PANE, "<red>Cancel", HumanEntity::closeInventory);

        return inv;
    }

    private void createInteractiveItems(Inventory inv, int[] slots, XMaterial material, String displayName, InteractiveItemClickHandler clickHandler) {
        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(material.parseItem()));
        item.setDisplayName(displayName);
        if (clickHandler != null) {
            item.onClick((player, clickType) -> clickHandler.handleClick(player));
        }
        for (int slot : slots) {
            inv.setItem(slot, item);
        }
    }

    @FunctionalInterface
    private interface InteractiveItemClickHandler {
        void handleClick(org.bukkit.entity.Player player);
    }
}
