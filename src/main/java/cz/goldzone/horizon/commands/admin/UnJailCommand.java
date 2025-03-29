package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.managers.JailManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnJailCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.jail")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/unjail <player>");
            return false;
        }

        Player targetPlayer = player.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found!");
            return false;
        }

        JailManager.unjail(targetPlayer);
        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have unjailed <red>" + targetPlayer.getName() + "<gray>.");
        targetPlayer.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been unjailed by <red>" + player.getName() + "<gray>.");

        return false;
    }
}
