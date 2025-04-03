package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RateGUI implements IGUI {
    private static final int[] RATING_SLOTS = {11, 12, 13, 14, 15};
    private final String warpName;

    public RateGUI(String warpName) {
        this.warpName = warpName;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, "Pick a rating");

        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        if (hasPlayerVisitedWarp(warpName)) {
            for (int i = 0; i < 5; i++) {
                final int rating = i + 1;
                InteractiveItem item = createRatingItem(rating);
                inventory.setItem(RATING_SLOTS[i], item);
            }
        } else {
            InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.BARRIER.parseItem()));
            item.setDisplayName("<red>You must visit this warp before rating!");
            item.setLore("<gray>\n<gray>Click to visit this player warp\n<gray>");
            item.onClick((player, clickType) -> {
                PlayerWarpsManager.teleportToPlayerWarp(player, warpName);
                player.closeInventory();
            });
            inventory.setItem(13, item);
        }

        return inventory;
    }

    private InteractiveItem createRatingItem(int rating) {
        String displayName = String.format("<yellow><bold>Rate</bold> " + warpName);
        String lore = String.format("<gray>\n<gray>Click to rate with <yellow>%d star%s\n<gray>", rating, (rating > 1 ? "s" : ""));
        if (rating == 1) {
            lore += "\n\n<red>Worst rating";
        } else if (rating == 5) {
            lore += "\n\n<green>Best rating";
        }

        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.NETHER_STAR.parseItem()));
        item.setDisplayName(displayName);
        item.setLore(lore);

        item.onClick((player, clickType) -> {
            int clampedRating = Math.min(rating, 5);
            PlayerWarpsManager.setPlayerWarpRating(warpName, clampedRating);
            player.closeInventory();
            player.sendMessage(Lang.getPrefix("PlayerWarps") + String.format("<gray>You rated the player warp <red>%s <gray>with <yellow>%d star%s", warpName, clampedRating, (clampedRating > 1 ? "s" : "")));

            if (clampedRating > 3) {
                String ownerName = PlayerWarpsManager.getPlayerWarpOwner(warpName);
                Player owner = Bukkit.getPlayer(ownerName);

                if (owner != null) {
                    String thankYouMessage = PlayerWarpsManager.getRateMessage(warpName);
                    owner.sendMessage(Lang.getPrefix("PlayerWarps") + Objects.requireNonNullElse(thankYouMessage, "<gray>Thank you for your rating!"));
                }
            }
        });

        return item;
    }

    private boolean hasPlayerVisitedWarp(String warpName) {
        return PlayerWarpsManager.getPlayerWarpVisitCount(warpName) > 0;
    }
}