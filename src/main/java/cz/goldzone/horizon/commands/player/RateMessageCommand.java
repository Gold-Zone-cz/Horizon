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
            return false;
        }

        if (args.length < 3) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage:\n <red>/ratemessage <player_warp_name> <stars> <message>");
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Example:\n <red>/ratemessage mypw 4 Thanks {player} for rating {warp}!");
            return false;
        }

        String warpName = args[0];
        int minStars = Integer.parseInt(args[1]);
        String thankYouMessage = constructThankYouMessage(args);

        String warpOwner = PlayerWarpsManager.getPlayerWarpOwner(warpName);
        if (!warpOwner.equalsIgnoreCase(player.getName())) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You must be the owner of this player warp!");
            return false;
        }

        PlayerWarpsManager.setRateMessage(warpName, thankYouMessage, minStars);
        thankYouMessage = thankYouMessage.replace("{player}", player.getName()).replace("{warp}", warpName);

        player.sendMessage(Lang.getPrefix("Horizon") + Lang.format(
                "<red>\"%{1}\" <gray>has been set for <gold>%{2}+ <gray>stars.", thankYouMessage, String.valueOf(minStars)));

        try {
            minStars = Integer.parseInt(args[1]);
            if (minStars < 1 || minStars > 5) {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>The rating must be between 1 and 5.");
                return false;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>The rating must be between 1 and 5.");
            return false;
        }
        return true;
    }

    private String constructThankYouMessage(String[] args) {
        return String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim();
    }
}