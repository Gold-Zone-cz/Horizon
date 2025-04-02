package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Slf4j
public class PlayerWarpsGUI implements IGUI {

    private static final int PAGE_SIZE = 28;
    private final Player player;
    private int page = 0;
    private final Category selectedCategory;

    public PlayerWarpsGUI(Player player, Category selectedCategory) {
        this.player = player;
        this.selectedCategory = selectedCategory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Player Warps - " + (selectedCategory != null ? selectedCategory.getDisplayName() : "All"));
        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        List<String> warpNames = getFilteredWarpNames();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, warpNames.size());

        for (int i = start; i < end; i++) {
            String warpName = warpNames.get(i);
            inventory.setItem(i - start, createWarpItem(warpName));
        }

        setupNavigationButtons(inventory, warpNames.size(), end);

        inventory.setItem(49, createButton("<green>All Player Warps", XMaterial.NETHER_STAR));
        return inventory;
    }

    private List<String> getFilteredWarpNames() {
        return PlayerWarpsManager.getPlayerWarps(player).stream()
                .filter(warpName -> selectedCategory == null || PlayerWarpsManager.getPlayerWarpCategory(warpName) == selectedCategory)
                .sorted(Comparator.comparingInt(warpName -> -PlayerWarpsManager.getPlayerWarpRating(warpName)))
                .toList();
    }

    private ItemStack createWarpItem(String warpName) {
        List<Integer> ratings = Collections.singletonList(PlayerWarpsManager.getPlayerWarpRating(warpName));
        int rating = getAverageRating(ratings);
        String warpStars = getStarRating(rating);
        ItemStack item = new ItemStack(Objects.requireNonNull(XMaterial.NETHER_STAR.parseItem()));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c" + warpName));

            String owner = PlayerWarpsManager.getPlayerWarpOwner(warpName);
            int visits = PlayerWarpsManager.getPlayerWarpVisitCount(warpName);

            List<String> lore = Arrays.asList(
                    " ",
                    "<gray>Owner: <red>" + (owner != null ? owner : "Unknown"),
                    "<gray>Visits: <red>" + visits,
                    " ",
                    "<gray>Rating: <gold>" + warpStars,
                    " ",
                    "<red>➥ " + "<gray>Left click to <red>teleport",
                    "<red>➥ " + "<gray>Right click to <gold>rate",
                    " "
            );
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void setupNavigationButtons(Inventory inventory, int totalWarps, int end) {
        if (page > 0) {
            inventory.setItem(45, createButton("<aqua>Previous Page", XMaterial.ARROW));
        }
        if (end < totalWarps) {
            inventory.setItem(53, createButton("<aqua>Next Page", XMaterial.ARROW));
        }
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (displayName.equalsIgnoreCase("Previous Page") && page > 0) {
            page--;
            player.openInventory(getInventory());
        } else if (displayName.equalsIgnoreCase("Next Page") && (page + 1) * PAGE_SIZE < PlayerWarpsManager.getPlayerWarps(player).size()) {
            page++;
            player.openInventory(getInventory());
        } else {
            if (event.isLeftClick()) {
                if (PlayerWarpsManager.teleportToPlayerWarp(player, displayName)) {
                    PlayerWarpsManager.incrementVisitCount(displayName);
                }
            } else if (event.isRightClick()) {
                player.openInventory(new RateGUI(displayName).getInventory());
            }
        }
    }

    private ItemStack createButton(String name, XMaterial material) {
        ItemStack item = new ItemStack(Objects.requireNonNull(material.parseItem()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private int getAverageRating(List<Integer> ratings) {
        return ratings.isEmpty() ? 0 : ratings.stream().mapToInt(Integer::intValue).sum() / ratings.size();
    }

    private String getStarRating(int rating) {
        int clampedRating = Math.max(1, Math.min(rating, 5));
        return "⭐".repeat(clampedRating) + "☆".repeat(5 - clampedRating);
    }
}
