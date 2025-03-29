package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.managers.FreezeManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreezeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.freeze")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Lang.getPrefix("Admin") + "<gray>Usage: <red>/freeze <player>");
            return false;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            sender.sendMessage(Lang.getPrefix("Admin") + "<red>You cannot freeze yourself!");
            return true;
        }


        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Lang.getPrefix("Admin") + "<red>Player is not available!");
            return true;
        }

        if (target.hasPermission("horizon.admin.freeze")) {
            sender.sendMessage(Lang.getPrefix("Admin") + "<red>You cannot freeze this player!");
            return true;
        }

        if (FreezeManager.isFrozen(target)) {
            FreezeManager.unfreezePlayer(target);
            sender.sendMessage(Lang.getPrefix("Admin") + "<green>Player " + target.getName() + " has been unfrozen.");
            target.sendMessage(Lang.getPrefix("Admin") + "<green>You have been unfrozen.");
        } else {
            FreezeManager.freezePlayer(target);
            sender.sendMessage(Lang.getPrefix("Admin") + "<red>Player " + target.getName() + " has been frozen.");
            target.sendMessage(Lang.getPrefix("Admin") + "<red>You have been frozen.\n<bold>Leaving the server will result in a ban.");
        }

        return true;
    }
}