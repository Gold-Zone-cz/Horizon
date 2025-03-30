package cz.goldzone.horizon.commands.global;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {

    private boolean setHat(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemStack currentHat = player.getInventory().getHelmet();

        if (itemInHand.getType().isAir()) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must hold an item in your hand!");
            return false;
        }

        if (currentHat != null && currentHat.getType() != Material.AIR) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(currentHat);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), currentHat);
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Inventory full!\nYour previous hat was dropped on the ground!");
            }
        }

        String itemName = formatItemName(itemInHand.getType());
        player.getInventory().setHelmet(itemInHand.clone());
        player.sendMessage(Lang.getPrefix("Horizon") + "<red>" + itemName + " <gray>has been set as your hat!");
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        return true;
    }

    private String formatItemName(Material material) {
        String itemName = material.toString().toLowerCase().replace("_", " ");
        return capitalizeFirstLetter(itemName);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (player.hasPermission("horizon.player.hat")) {
            return setHat(player);
        } else {
            player.sendMessage(Lang.getPrefix("VIP") + "<red>You need VIP rank to use this command!\nUse /vip for more information.");
            return true;
        }
    }
}