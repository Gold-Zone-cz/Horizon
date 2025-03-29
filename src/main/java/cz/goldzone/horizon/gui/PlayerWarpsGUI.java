package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import dev.digitality.digitalgui.api.IGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PlayerWarpsGUI implements IGUI {

    private static final int PAGE_SIZE = 28;
    private final Player player;
    private int page = 0;
    private Category selectedCategory;

    public PlayerWarpsGUI(Player player, Category selectedCategory) {
        this.player = player;
        this.selectedCategory = selectedCategory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Player Warps");

        List<String> warps = PlayerWarpsManager.getPlayerWarps(player).stream()
                .filter(warp -> selectedCategory == null || PlayerWarpsManager.getPlayerWarpCategory(warp) == selectedCategory)
                .toList();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, warps.size());

        for (int i = start; i < end; i++) {
            String warpName = warps.get(i);
            ItemStack item = new ItemStack(Objects.requireNonNull(XMaterial.ENDER_PEARL.parseItem()));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + warpName));
                item.setItemMeta(meta);
            }
            inventory.setItem(i - start, item);
        }

        if (page > 0) {
            inventory.setItem(45, createButton("&aPrevious Page", XMaterial.ARROW));
        }
        if (end < warps.size()) {
            inventory.setItem(53, createButton("&aNext Page", XMaterial.ARROW));
        }

        for (Category category : Category.values()) {
            inventory.setItem(category.getSlot(), createButton(category.getDisplayName(), XMaterial.matchXMaterial(category.getMaterial())));
        }

        return inventory;
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (displayName.equalsIgnoreCase("Previous Page")) {
            if (page > 0) {
                page--;
                player.openInventory(getInventory());
            }
        } else if (displayName.equalsIgnoreCase("Next Page")) {
            if ((page + 1) * PAGE_SIZE < PlayerWarpsManager.getPlayerWarps(player).size()) {
                page++;
                player.openInventory(getInventory());
            }
        } else {
            for (Category category : Category.values()) {
                if (displayName.equalsIgnoreCase(ChatColor.stripColor(category.getDisplayName()))) {
                    selectedCategory = category;
                    page = 0;
                    player.openInventory(getInventory());
                    return;
                }
            }

            PlayerWarpsManager.teleportToPlayerWarp(player, displayName);
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
}
