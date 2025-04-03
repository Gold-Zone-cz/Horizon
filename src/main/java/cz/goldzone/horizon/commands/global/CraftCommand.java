package cz.goldzone.horizon.commands.global;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class CraftCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (player.hasPermission("horizon.player.craft")) {
            player.openInventory(Bukkit.createInventory(player, InventoryType.WORKBENCH));
            player.playSound(player.getLocation(), "block.crafter.craft", 1, 1);
        } else {
            player.sendMessage(Lang.getPrefix("VIP") + "<red>You need VIP rank to use this command!\nUse /vip for more information.");
        }

        return true;
    }
}
