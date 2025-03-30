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

    private static boolean isPositive(final String number) {
        try {
            return Long.parseLong(number) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void freeze(Player player, final int minutes, final String staff) {
        if (minutes == 0) {
            FreezeManager.unfreezePlayer(player);
        } else {
            FreezeManager.freezePlayer(player, minutes, staff, true);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.staff.freeze")) {
            player.sendMessage(Lang.getPrefix("Freeze") + Lang.format(Lang.get("core.no_perm", player)));
            return false;
        }

        if (args.length != 2) {
            player.sendMessage(Lang.getPrefix("Admin") + "<gray>Usage: <red>/freeze <player> <minutes>");
            return true;
        }

        final String targetName = args[0];
        final String timeArg = args[1];
        final Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>Player not found!");
            return false;
        }

        if (target == player) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>You cannot freeze yourself!");
            return false;
        }

        if (!isPositive(timeArg)) {
            player.sendMessage(Lang.getPrefix("Admin") + "<red>Invalid number!");
            return false;
        }

        int minutes = Math.min(Integer.parseInt(timeArg), 10);
        freeze(target, minutes, player.getName());

        String freezeMessage = minutes == 0 ?
                "<gray>Staff <red>" + player.getName() + " <gray>unfroze <red>" + target.getName() :
                "<gray>Staff <red>" + player.getName() + " <gray>froze <red>" + target.getName() + " <gray>for <red>" + minutes + " <gray>minutes.";
        Bukkit.broadcastMessage(Lang.getPrefix("Notify") + freezeMessage);

        return true;
    }
}