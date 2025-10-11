package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.gui.InvseeGUI;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.admin.invsee")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/invsee <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>That player is not online.");
            return false;
        }

        new InvseeGUI(target).open(player);
        return true;
    }
}
