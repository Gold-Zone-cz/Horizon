package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        Player target;
        if (args.length == 0) {
            target = player;
        } else {
            if (!player.hasPermission("horizon.staff.playtime")) {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>You don't have permission to do that.");
                return true;
            }

            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found or not online.");
                return true;
            }
        }

        long ticksPlayed = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long totalSeconds = ticksPlayed / 20L;

        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String timeStr = days + "d " + hours + "h " + minutes + "m " + seconds + "s";

        String message;
        if (player.equals(target)) {
            message = Lang.getPrefix("Horizon") + "<gray>Your playtime: <red>" + timeStr;
        } else {
            message = Lang.getPrefix("Horizon") + "<red>" + target.getName() + "<gray>'s playtime: <red>" + timeStr;
        }

        player.sendMessage(message);
        return true;
    }
}
