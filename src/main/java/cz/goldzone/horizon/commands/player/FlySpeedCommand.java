package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlySpeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.player.flyspeed")) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You don't have permission to use this command.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/flyspeed <0-10>");
            return false;
        }

        try {
            float speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Speed must be between 0 and 10!");
                return false;
            }
            player.setFlySpeed(speed / 10);
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your fly speed has been set to <red>" + speed);
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Invalid number format!");
        }

        return true;
    }
}
