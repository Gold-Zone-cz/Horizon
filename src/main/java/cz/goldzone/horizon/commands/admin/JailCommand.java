package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.managers.JailManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class JailCommand implements CommandExecutor {
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

        if (args.length < 2) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/jail <player> <duration> [reason]");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found!");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        int duration;
        try {
            duration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Invalid duration! Please enter a valid number.");
            return false;
        }

        String reason = args.length > 2 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No reason provided";

        JailManager.jail(target, duration, reason, player.getName());

        if (target != null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have jailed <red>" + target.getName() + " <green>for <red>" + duration + " <gray>minutes.");
        }
        return true;
    }
}
