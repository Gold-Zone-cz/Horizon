package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RateGUI implements IGUI {
    private static final int INVENTORY_SIZE = 27;
    private static final int MAX_RATING = 5;
    private final String warpName;

    public RateGUI(String warpName) {
        this.warpName = warpName;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, INVENTORY_SIZE, "Rate " + warpName);

        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        if (hasPlayerVisitedWarp(warpName)) {
            for (int i = 0; i < MAX_RATING; i++) {
                final int rating = i + 1;

                InteractiveItem item = createRatingItem(rating);
                inventory.setItem(i, item);
            }
        } else {
            InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.BARRIER.parseItem()));
            item.setDisplayName("<red>You must visit this warp before rating!");
            inventory.setItem(13, item);
        }

        return inventory;
    }

    private InteractiveItem createRatingItem(int rating) {
        String displayName = String.format("<yellow>%d star%s", rating, (rating > 1 ? "s" : ""));
        String lore = String.format("<gray>Click to rate this player warp with\n%d star%s", rating, (rating > 1 ? "s" : ""));

        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.NETHER_STAR.parseItem()));
        item.setDisplayName(displayName);
        item.setLore(lore);

        item.onClick((player, clickType) -> {
            int clampedRating = Math.min(rating, MAX_RATING);
            PlayerWarpsManager.setPlayerWarpRating(warpName, clampedRating);
            player.closeInventory();
            player.sendMessage(String.format("<green>You rated the warp %s with %d star%s", warpName, clampedRating, (clampedRating > 1 ? "s" : "")));
        });

        return item;
    }

    private boolean hasPlayerVisitedWarp(String warpName) {
        return PlayerWarpsManager.getPlayerWarpVisitCount(warpName) > 0;
    }
}
