package cz.goldzone.horizon.commands.home;

import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Lang.getPrefix("Homes") + "<red>You must provide a home name!");
            return false;
        }

        String homeName = args[0].toLowerCase();
        Location location = player.getLocation();

        List<String> homes = HomesManager.getHomes(player);
        int maxHomes = getMaxHomes(player);

        if (homes.size() >= maxHomes) {
            player.sendMessage(Lang.getPrefix("Homes") + "<red>You have reached your home limit (" + maxHomes + ")!");
            return false;
        }

        HomesManager.setHome(player, homeName, location);
        player.sendMessage(Lang.getPrefix("Homes") + "<gray>Home <red>" + homeName + "<gray> has been set!");
        return true;
    }

    private int getMaxHomes(Player player) {
        if (player.hasPermission("horizon.homes.7")) {
            return 7;
        } else if (player.hasPermission("horizon.homes.5")) {
            return 5;
        } else if (player.hasPermission("horizon.homes.3")) {
            return 3;
        }
        return 2;
    }
}
