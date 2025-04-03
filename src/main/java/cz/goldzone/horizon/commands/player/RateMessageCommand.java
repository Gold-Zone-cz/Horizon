package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RateMessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length < 3) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/ratemessage <player_warp_name> <stars> <message>");
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Example: <red>/ratemessage mypwarp 4 Thanks {player} for rating my warp {warp}!");
            return true;
        }

        String warpName = args[0];
        int ratingThreshold;

        try {
            ratingThreshold = Integer.parseInt(args[1]);
            if (ratingThreshold < 1 || ratingThreshold > 5) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>The rating threshold must be a number between 1 and 5.");
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (message.isEmpty()) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must provide a message.");
            return true;
        }

        String ownerName = PlayerWarpsManager.getPlayerWarpOwner(warpName);
        if (ownerName == null || !ownerName.equalsIgnoreCase(player.getName())) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must be the owner of this warp to set a thank you message.");
            return false;
        }

        PlayerWarpsManager.setRateMessage(warpName, message, ratingThreshold);

        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your thank you message has been set for ratings " + ratingThreshold + "+ stars.");
        return true;
    }
}
