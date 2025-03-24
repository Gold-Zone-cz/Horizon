package cz.goldzone.horizon.commands.home;

import cz.goldzone.horizon.gui.HomesGUI;
import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeListCommand implements CommandExecutor {
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

        player.openInventory(new HomesGUI(homes, player).getInventory());

        return true;
    }
}