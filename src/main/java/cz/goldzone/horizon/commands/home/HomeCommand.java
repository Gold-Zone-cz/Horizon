package cz.goldzone.horizon.commands.home;

import cz.goldzone.horizon.gui.HomesGUI;
import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        List<String> homes = HomesManager.getHomes(player);

        if (homes.isEmpty()) {
            player.sendMessage(Lang.getPrefix("Homes") + "<red>You don't have any homes set!");
            return true;
        }

        if (homes.size() == 1) {
            String homeName = homes.getFirst();
            teleportToHome(player, homeName);
            return true;
        }

        if (args.length == 0) {
            player.openInventory(new HomesGUI(homes, player).getInventory());
            return true;
        }

        if (args.length == 1) {
            String homeName = args[0].toLowerCase();
            if (homes.contains(homeName)) {
                teleportToHome(player, homeName);
            } else {
                player.sendMessage(Lang.getPrefix("Homes") + "<red>Invalid home name! Use <white>/homes <red>to list your homes.");
            }
            return true;
        }

        return false;
    }

    private void teleportToHome(Player player, String homeName) {
        Location homeLocation = HomesManager.getHome(player, homeName);
        if (homeLocation != null) {
            player.teleport(homeLocation);
            player.sendMessage(Lang.getPrefix("Homes") + "<gray>You have been teleported to home <red>" + homeName + "<gray>!");
        } else {
            player.sendMessage(Lang.getPrefix("Homes") + "<red>Could not find the location of the home!");
        }
    }
}

