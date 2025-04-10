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
    private final Player player;

    public RateGUI(String warpName, Player player) {
        this.warpName = warpName;
        this.player = player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, "Pick a rating");

        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        if (hasPlayerVisitedWarp(warpName, player)) {
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
                PlayerWarpsManager.markWarpAsVisited(player, warpName);
                player.closeInventory();
            });
            inventory.setItem(13, item);
        }

        return inventory;
    }

    private InteractiveItem createRatingItem(int rating) {
        String displayName = "<yellow><bold>Rate</bold> " + warpName;
        String lore = Lang.format(
                "<gray>\n<gray>Click to rate with <yellow>%{1} star%{2}\n<gray>",
                String.valueOf(rating), (rating > 1 ? "s" : "")
        );
        if (rating == 1) {
            lore += "\n<red>Worst rating!";
        } else if (rating == 5) {
            lore += "\n<green>Great rating!";
        } else if (rating == 3) {
            lore += "\n<yellow>Average rating!";
        }

        InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.NETHER_STAR.parseItem()));
        item.setDisplayName(displayName);
        item.setLore(lore);

        item.onClick((player, clickType) -> {
            int clampedRating = Math.min(rating, 5);
            PlayerWarpsManager.setPlayerWarpRating(warpName, clampedRating);
            player.closeInventory();
            player.sendMessage(Lang.getPrefix("PlayerWarps") + Lang.format("<gray>You rated the player warp <red>%{1} <gray>with <yellow>%{2} star%{3}", warpName, String.valueOf(clampedRating), (clampedRating > 1 ? "s" : "")));

            sendThankYouMessages(warpName, clampedRating, player);
        });

        return item;
    }

    public static void sendThankYouMessages(String warpName, int givenRating, Player ratingPlayer) {
        String message = PlayerWarpsManager.getRateMessage(warpName);
        int requiredThreshold = PlayerWarpsManager.getRateThreshold(warpName);

        if (message == null || message.isEmpty() || givenRating < requiredThreshold) {
            return;
        }

        String formattedMessage = message.replace("{player}", ratingPlayer.getName()).replace("{warp}", warpName);
        ratingPlayer.sendMessage(Lang.getPrefix("Rate") + "<gray>" + formattedMessage);
    }

    private boolean hasPlayerVisitedWarp(String warpName, Player player) {
        return PlayerWarpsManager.hasVisitedPlayerWarp(player, warpName);
    }
}
