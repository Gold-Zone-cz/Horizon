package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PlayerWarpsGUI implements IGUI {
    
    private final int PAGE_SIZE = 28;

    private final Player player;
    private static Category category;
    private int page = 0;

    private static final String ALL_WARPS_BUTTON_NAME = ChatColor.stripColor(
            ChatColor.translateAlternateColorCodes('&', "<green>All Player Warps")
    );

    public static final Map<String, ItemStack> cachedWarpItems = new ConcurrentHashMap<>();

    public PlayerWarpsGUI(Player player, Category category) {
        this.player = player;
        PlayerWarpsGUI.category = category;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Player Warps - " + getCategoryName());
        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        List<String> warps = PlayerWarpsManager.getAllPlayerWarps().stream()
                .filter(warp -> PlayerWarpsManager.getPlayerWarpCategory(warp) == category)
                .toList();

        populateWarpItems(inventory, warps);
        setupNavigationButtons(inventory, warps.size());
        inventory.setItem(49, createAllWarpsButton());

        return inventory;
    }

    private String getCategoryName() {
        return category != null ? category.getDisplayName() : "All";
    }

    private int getPlayerWarpSlot(int index) {
        int slotOffset = 10 + (index % PAGE_SIZE) + (index / PAGE_SIZE);
        return (slotOffset == 17) ? 19 : slotOffset;
    }

    private void populateWarpItems(Inventory inventory, List<String> warps) {
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, warps.size());

        for (int i = start; i < end; i++) {
            int slot = getPlayerWarpSlot(i - start);
            String warpName = warps.get(i);

            InteractiveItem item = createWarpItem(warpName);
            inventory.setItem(slot, item);
        }
    }

    private InteractiveItem createWarpItem(String warpName) {
        if (cachedWarpItems.containsKey(warpName)) {
            return new InteractiveItem(cachedWarpItems.get(warpName));
        }

        int rating = PlayerWarpsManager.getPlayerWarpRating(warpName);
        String stars = getStarRating(rating);
        int visits = PlayerWarpsManager.getPlayerWarpVisitCount(warpName);
        String ownerName = PlayerWarpsManager.getPlayerWarpOwner(warpName);

        if (ownerName == null) {
            log.warn("Warp '{}' has no owner! Skipping item creation.", warpName);
            return null;
        }

        ItemStack item = PlayerWarpsManager.getCachedPlayerWarpItem(warpName, category);
        if (item == null) {
            throw new IllegalArgumentException("Item not found for warp: " + warpName);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String color = ChatColor.translateAlternateColorCodes('&', getColorForCategory());
            meta.setDisplayName(color + warpName);
            meta.setLore(createWarpLore(ownerName, visits, stars, color));
            item.setItemMeta(meta);
        }

        cachedWarpItems.put(warpName, item);

        InteractiveItem interactiveItem = new InteractiveItem(item);
        interactiveItem.onClick((player, clickType) -> {
            String rawWarpName = ChatColor.stripColor(warpName);
            String currentPlayerName = player.getName();

            if (clickType.isLeftClick()) {
                PlayerWarpsManager.teleportToPlayerWarp(player, rawWarpName);
            } else if (clickType.isRightClick()) {
                if (ownerName.equals(currentPlayerName)) {
                    player.sendMessage(Lang.getPrefix("PlayerWarps") + "<red>You cannot rate your own player warp!");
                    return;
                }
                player.openInventory(new RateGUI(warpName, player).getInventory());
            }
        });

        return interactiveItem;
    }

    public static String getColorForCategory() {
        return switch (category) {
            case FARMS -> "&e";
            case SHOPS -> "&a";
            case MISC -> "&b";
        };
    }

    public static XMaterial getRandomMaterialForCategory(Category category) {
        return switch (category) {
            case FARMS -> getRandomFrom(XMaterial.WITHER_ROSE, XMaterial.END_CRYSTAL, XMaterial.CROSSBOW, XMaterial.FLINT_AND_STEEL);
            case SHOPS -> getRandomFrom(XMaterial.EMERALD, XMaterial.GOLD_NUGGET, XMaterial.CHEST, XMaterial.SUNFLOWER);
            case MISC -> getRandomFrom(XMaterial.BOOK, XMaterial.ENDER_EYE, XMaterial.NOTE_BLOCK, XMaterial.BONE);
        };
    }

    private static XMaterial getRandomFrom(XMaterial... materials) {
        return materials[(int) (Math.random() * materials.length)];
    }

    private List<String> createWarpLore(String ownerName, int visits, String stars, String color) {
        return List.of(
                " \n",
                formatLine("Owner ", ownerName, color),
                formatLine("Visits ", String.valueOf(visits), color),
                " \n",
                "<gray>Rating <gold>" + stars,
                " \n",
                color + "➥ <gray>Left click to" + color + " teleport",
                color + "➥ <gray>Right click to <gold>rate",
                " \n"
        );
    }

    private String formatLine(String label, String value, String color) {
        return Lang.format("%{1}<gray>%{2}" + color + "%{3}", color, label, value);
    }

    private void setupNavigationButtons(Inventory inventory, int total) {
        if (page > 0) {
            createNavigationButton(inventory, 45, "<aqua>Previous Page", "<gray>Click to go to the previous page", -1);
        }
        if (page * PAGE_SIZE + PAGE_SIZE < total) {
            createNavigationButton(inventory, 53, "<aqua>Next Page", "<gray>Click to go to the next page", 1);
        }
    }

    private void createNavigationButton(Inventory inventory, int slot, String displayName, String lore, int pageChange) {
        InteractiveItem button = new InteractiveItem(Objects.requireNonNull(XMaterial.ARROW.parseItem()));
        button.setDisplayName(displayName);
        button.setLore(lore);
        button.onClick((player, clickType) -> {
            page += pageChange;
            player.openInventory(getInventory());
        });
        inventory.setItem(slot, button);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getItemMeta() == null) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("<aqua>Previous Page") && page > 0) {
            page--;
            player.openInventory(getInventory());
            return;
        }

        if (name.equalsIgnoreCase("<aqua>Next Page")) {
            if (page * PAGE_SIZE + PAGE_SIZE < PlayerWarpsManager.getAllPlayerWarps().size()) {
                page++;
            }
            player.openInventory(getInventory());
            return;
        }

        if (name.equalsIgnoreCase(ALL_WARPS_BUTTON_NAME)) {
            player.openInventory(new AllPlayerWarpsGUI(player).getInventory());
        }
    }

    private ItemStack createAllWarpsButton() {
        ItemStack item = new ItemStack(Objects.requireNonNull(XMaterial.NETHER_STAR.parseItem()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(ALL_WARPS_BUTTON_NAME)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private String getStarRating(int rating) {
        int stars = Math.max(1, Math.min(rating, 5));
        return "⭐".repeat(stars) + "☆".repeat(5 - stars);
    }
}