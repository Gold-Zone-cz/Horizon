package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.managers.CooldownManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {

    private static final long COOLDOWN = 5 * 60 * 1000;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.player.feed")) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You don't have permission to use this command.");
            return false;
        }


        if (CooldownManager.isInCooldown(player, "feed", COOLDOWN)) {
            long remaining = CooldownManager.getRemaining(player, "feed", COOLDOWN);
            long seconds = remaining / 1000;
            long minutes = seconds / 60;
            seconds %= 60;

            player.sendMessage(Lang.getPrefix("Horizon") +
                    "<red>You can use feed again in <yellow>" + minutes + "m " + seconds + "s");
            return true;
        }

        player.setFoodLevel(20);
        player.setSaturation(20);

        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been fed.");
        CooldownManager.setCooldown(player, "feed");

        return true;
    }
}
