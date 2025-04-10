package cz.goldzone.horizon.commands.admin;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemCommand implements CommandExecutor {

    private static final Map<String, ItemStack> itemMap = new HashMap<>();

    public ItemCommand() {
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
        int quantity = parseQuantity(args, player);
        if (quantity <= 0) return false;

        ItemStack item = getItemFromName(itemName, quantity);
        if (item == null) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>Item not found.");
            return false;
        }

        quantity = Math.min(quantity, item.getMaxStackSize());
        item.setAmount(quantity);
        player.getInventory().addItem(item);
        player.sendMessage(Lang.getPrefix("Admin") + "<gray>You have received <red>x" + quantity + " " + getDisplayName(item));
        return true;
    }

    private int parseQuantity(String[] args, Player player) {
        if (args.length > 1) {
            try {
                int quantity = Integer.parseInt(args[1]);
                if (quantity < 1) {
                    player.sendMessage(Lang.getPrefix("Admin") + "<red>Invalid quantity. Please enter a number greater than 0.");
                    return -1;
                }
                return quantity;
            } catch (NumberFormatException e) {
                player.sendMessage(Lang.getPrefix("Admin") + "<red>Invalid quantity. Please enter a valid number.");
                return -1;
            }
        }
        return 1;
    }

    private ItemStack getItemFromName(String itemName, int quantity) {
        ItemStack item = itemMap.get(itemName);
        if (item != null) {
            return item.clone();
        }

        Material material = Material.getMaterial(itemName.toUpperCase());
        return (material != null) ? new ItemStack(material, Math.min(quantity, material.getMaxStackSize())) : null;
    }

    private String getDisplayName(ItemStack item) {
        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return formatMaterialName(item.getType().name());
    }

    private String formatMaterialName(String materialName) {
        String[] parts = materialName.toLowerCase().split("_");
        StringBuilder formattedName = new StringBuilder();

        for (String part : parts) {
            formattedName.append(part.substring(0, 1).toUpperCase()).append(part.substring(1)).append(" ");
        }

        return formattedName.toString().trim();
    }
}