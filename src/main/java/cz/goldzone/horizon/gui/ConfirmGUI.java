package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class ConfirmGUI implements IGUI {
    private final Runnable onConfirm;

    public ConfirmGUI(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Are you sure?");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        int[] confirmSlots = {0, 9, 18, 1, 10, 19, 2, 11, 20};
        int[] cancelSlots = {6, 15, 24, 7, 16, 25, 8, 17, 26};

        for (int confirmSlot : confirmSlots) {
            InteractiveItem confirm = new InteractiveItem(Objects.requireNonNull(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()), confirmSlot);
            confirm.setDisplayName("<green>Confirm");
            confirm.onClick((player, clickType) -> {
                player.closeInventory();
                onConfirm.run();
            });
            inv.setItem(confirm.getSlot(), confirm);
        }

        for (int cancelSlot : cancelSlots) {
            InteractiveItem cancel = new InteractiveItem(Objects.requireNonNull(XMaterial.RED_STAINED_GLASS_PANE.parseItem()), cancelSlot);
            cancel.setDisplayName("<red>Cancel");
            cancel.onClick((player, clickType) -> {
                player.closeInventory();
            });
            inv.setItem(cancel.getSlot(), cancel);
        }

        return inv;
    }
}
