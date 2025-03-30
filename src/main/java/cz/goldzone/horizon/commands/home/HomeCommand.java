package cz.goldzone.horizon.commands.home;

import cz.goldzone.horizon.gui.HomesGUI;
import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        List<String> homes = HomesManager.getHomes(player);
        if (homes.isEmpty()) {
            player.sendMessage(Lang.getPrefix("Homes") + "<red>You don't have any homes set!");
            return true;
        }

        switch (args.length) {
            case 0:
                player.openInventory(new HomesGUI(homes, player).getInventory());
                return true;

            case 1:
                String homeName = args[0].toLowerCase();
                Set<String> homeSet = new HashSet<>(homes);
                if (homeSet.contains(homeName)) {
                    teleportToHome(player, homeName);
                } else {
                    player.sendMessage(Lang.getPrefix("Homes") + "<red>Invalid home name! Use <white>/homes <red>to list your homes.");
                }
                return true;

            default:
                return false;
        }
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

