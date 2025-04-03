package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import cz.goldzone.horizon.managers.PlayerWarpsManager;

import java.util.List;


public class RateMessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/ratemessage <player_warp_name> <message>");
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Example: <red>/ratemessage mypwarp Thanks {player} for rating my warp {warp}! ");
            return true;
        }

        String warpName = args[0];
        int ratingThreshold;

        try {
            ratingThreshold = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>The rating threshold must be a valid number.");
            return true;
        }

        // String message = String.join(" ", args, 2, args.length);
        // if (message.isEmpty()) {
        //    player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must provide a message.");
        //    return true;
        //}

        String ownerName = PlayerWarpsManager.getPlayerWarpOwner(warpName);
        if (ownerName == null || !ownerName.equals(player.getName())) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must be the owner of this warp to set a thank you message.");
            return false;
        }

        for (Player ratingPlayer : Bukkit.getOnlinePlayers()) {
            int rating = PlayerWarpsManager.getPlayerWarpRating(warpName);
            if (rating > 3) {
                String message = PlayerWarpsManager.getRateMessage(warpName);
                if (message != null && !message.isEmpty()) {
                    String thankYouMessage = message.replace("{player}", ratingPlayer.getName()).replace("{warp}", warpName);
                    ratingPlayer.sendMessage(Lang.getPrefix("Horizon") + "<gray>" + thankYouMessage);
                } else {
                    ratingPlayer.sendMessage(Lang.getPrefix("Horizon") + "<gray>Thank you for your feedback!");
                }
            }
        }

        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your thank you message has been set." +
                (ratingThreshold == 1 ? "1+" : ratingThreshold + "+") + " stars.");
        return true;
    }
}
