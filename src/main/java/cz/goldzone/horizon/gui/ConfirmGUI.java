package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class ConfirmGUI implements IGUI {
    private final Runnable onConfirm;

    public ConfirmGUI(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Are you sure?");

        int[] confirmSlots = {0, 9, 18, 1, 10, 19, 2, 11, 20};
        int[] emptySlots = {3, 12, 21, 4, 13, 22, 5, 14, 23};
        int[] cancelSlots = {6, 15, 24, 7, 16, 25, 8, 17, 26};

        for (int confirmSlot : confirmSlots) {
            InteractiveItem confirm = new InteractiveItem(Objects.requireNonNull(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()));
            confirm.setDisplayName("<green>Confirm");
            confirm.onClick((player, clickType) -> {
                player.closeInventory();
                onConfirm.run();
            });
            inv.setItem(confirmSlot, confirm);
        }

        for (int emptySlot : emptySlots) {
            InteractiveItem empty = new InteractiveItem(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()));
            empty.setDisplayName(" ");
            inv.setItem(emptySlot, empty);
        }

        Arrays.stream(cancelSlots).forEach(cancelSlot -> {
            InteractiveItem cancel = new InteractiveItem(Objects.requireNonNull(XMaterial.RED_STAINED_GLASS_PANE.parseItem()));
            cancel.setDisplayName("<red>Cancel");
            cancel.onClick((player, clickType) -> player.closeInventory());
            inv.setItem(cancelSlot, cancel);
        });

        return inv;
    }
}
