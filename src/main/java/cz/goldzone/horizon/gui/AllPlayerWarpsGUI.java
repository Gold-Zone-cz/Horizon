package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
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
import java.util.Optional;

public class AllPlayerWarpsGUI implements IGUI {

    private final Player player;
    private int page = 0;

    public AllPlayerWarpsGUI(Player player) {
        this.player = player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "All Player Warps");
        DigitalGUI.fillInventory(inventory, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        List<String> warps = PlayerWarpsManager.getPlayerWarps(player);
        populateWarps(inventory, warps);
        setupNavigationButtons(inventory, warps.size());
        addBackItem(inventory);

        return inventory;
    }

    private void populateWarps(Inventory inventory, List<String> warps) {
        int start = page * 28;
        int end = Math.min(start + 28, warps.size());

        int[] slots = getWarpSlots();

        for (int i = start, slotIndex = 0; i < end && slotIndex < slots.length; i++, slotIndex++) {
            Optional<ItemStack> cachedItemOptional = Optional.ofNullable(PlayerWarpsGUI.cachedWarpItems.get(warps.get(i)));
            if (cachedItemOptional.isPresent()) {
                inventory.setItem(slots[slotIndex], createWarpItem(warps.get(i)));
            }
        }
    }

    private InteractiveItem createWarpItem(String warpName) {
        String owner = PlayerWarpsManager.getPlayerWarpOwner(warpName);
        int visits = PlayerWarpsManager.getPlayerWarpVisitCount(warpName);
        int rating = PlayerWarpsManager.getPlayerWarpRating(warpName);
        Category category = PlayerWarpsManager.getPlayerWarpCategory(warpName);
        ItemStack item = Optional.ofNullable(PlayerWarpsManager.getCachedPlayerWarpItem(warpName, category))
                .orElse(XMaterial.STONE.parseItem());

        ItemMeta meta = Objects.requireNonNull(item).getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + warpName);
            meta.setLore(createLore(owner, visits, rating, category));
            item.setItemMeta(meta);
        }

        InteractiveItem interactiveItem = new InteractiveItem(item);
        interactiveItem.onClick((player, clickType) -> {
            if (clickType.isLeftClick()) {
                PlayerWarpsManager.teleportToPlayerWarp(player, warpName);
            } else if (clickType.isRightClick()) {
                if (player.getName().equalsIgnoreCase(owner)) {
                    player.sendMessage(Lang.format("<red>You cannot rate your own warp!"));
                    return;
                }
                player.openInventory(new RateGUI(warpName, player).getInventory());
            }
        });

        return interactiveItem;
    }

    private List<String> createLore(String owner, int visits, int rating, Category category) {
        return List.of(
                " ",
                formatLine("Category", category.getDisplayName()),
                " ",
                formatLine("Owner", owner),
                " ",
                formatLine("Visits", String.valueOf(visits)),
                " ",
                "<gray>Rating <gold>" + getStarRating(rating),
                " ",
                "<green>➥ <gray>Left click to <green>teleport",
                "<green>➥ <gray>Right click to <gold>rate",
                " "
        );
    }

    private void setupNavigationButtons(Inventory inventory, int totalWarps) {
        setupNavigationButton(inventory, 45, "<aqua>Previous Page", "<gray>Click to go to the previous page", () -> {
            if (page > 0) {
                page--;
                player.openInventory(getInventory());
            }
        });

        setupNavigationButton(inventory, 53, "<aqua>Next Page", "<gray>Click to go to the next page", () -> {
            if ((page + 1) * 28 < totalWarps) {
                page++;
                player.openInventory(getInventory());
            }
        });
    }

    private void setupNavigationButton(Inventory inventory, int slot, String displayName, String lore, Runnable action) {
        if (action != null) {
            InteractiveItem button = new InteractiveItem(Objects.requireNonNull(XMaterial.ARROW.parseItem()));
            button.setDisplayName(displayName);
            button.setLore(lore);
            button.onClick((p, clickType) -> action.run());
            inventory.setItem(slot, button);
        }
    }

    private void addBackItem(Inventory inventory) {
        InteractiveItem back = new InteractiveItem(Objects.requireNonNull(XMaterial.BARRIER.parseItem()));
        back.setDisplayName("<red>Back");
        back.setLore("<gray>Click to go back");
        back.onClick((p, clickType) -> p.openInventory(new CategoriesGUI().getInventory()));
        inventory.setItem(49, back);
    }

    private String getStarRating(int rating) {
        int stars = Math.max(1, Math.min(rating, 5));
        return "⭐".repeat(stars) + "☆".repeat(5 - stars);
    }

    private String formatLine(String label, String value) {
        return Lang.format("<gray>%{1}: <green>%{2}", label, value);
    }

    public void handleClick(InventoryClickEvent event) {
        if (!(event.getCurrentItem() instanceof InteractiveItem)) {
            event.setCancelled(true);
        }
    }

    private int[] getWarpSlots() {
        return new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
    }
}