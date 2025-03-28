package cz.goldzone.horizon.commands.admin;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemCommand implements CommandExecutor {

    private static final Map<String, ItemStack> itemMap = new HashMap<>();

    public ItemCommand() {
        // custom items
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.economy")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Lang.getPrefix("Admin") + "<gray>Usage: <red>/i <item> [quantity]");
            return false;
        }

        String itemName = args[0].toLowerCase();
        int quantity = 1;

        if (args.length > 1) {
            try {
                quantity = Integer.parseInt(args[1]);

                if (quantity < 1) {
                    player.sendMessage(Lang.getPrefix("Admin") + "<red>Invalid quantity. Please enter a number greater than 0.");
                    return false;
                }

            } catch (NumberFormatException e) {
                player.sendMessage(Lang.getPrefix("Admin") + "<red>Invalid quantity. Please enter a valid number.");
                return false;
            }
        }

        ItemStack item = getItemFromName(itemName, quantity);

        if (item == null) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>Item not found.");
            return false;
        }

        int maxStackSize = item.getMaxStackSize();
        if (quantity > maxStackSize) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>Max stack size for this item is " + maxStackSize + ". You will receive " + maxStackSize + " instead.");
            quantity = maxStackSize;
        }

        String displayName = getDisplayName(item);

        item.setAmount(quantity);
        player.getInventory().addItem(item);
        player.sendMessage(Lang.getPrefix("Admin") + "<gray>You have received <red>" + quantity + "x <gray>" + displayName);
        return true;
    }

    private ItemStack getItemFromName(String itemName, int quantity) {
        if (itemMap.containsKey(itemName)) {
            ItemStack item = itemMap.get(itemName).clone();
            item.setAmount(Math.min(quantity, item.getMaxStackSize()));
            return item;
        }

        Material material = Material.getMaterial(itemName.toUpperCase());
        if (material != null) {
            return new ItemStack(material, Math.min(quantity, material.getMaxStackSize()));
        }

        return null;
    }

    private String getDisplayName(ItemStack item) {
        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return "<gray>" + formatMaterialName(item.getType().name());
    }

    private String formatMaterialName(String materialName) {
        return materialName.toLowerCase().replace("_", " ");
    }
}
