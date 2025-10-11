package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerWeatherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.player.pweather")) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You don't have permission to use this command.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/pweather <clear|rain");
            return false;
        }

        String weatherType = args[0].toLowerCase();
        switch (weatherType) {
            case "clear" -> {
                player.setPlayerWeather(WeatherType.CLEAR);
                player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your personal weather has been set to <yellow>clear");
                return true;
            }
            case "rain" -> {
                player.setPlayerWeather(WeatherType.DOWNFALL);
                player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your personal weather has been set to <aqua>rain");
                return true;
            }
            default -> {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Invalid weather type! Use <yellow>clear, rain, or thunder");
                return false;
            }
        }
    }
}
