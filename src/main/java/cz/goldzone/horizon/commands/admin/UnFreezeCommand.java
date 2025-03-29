package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.managers.FreezeManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnFreezeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.freeze")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (args.length == 1) {
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Lang.getPrefix("Admin") + "<red>Player not found!");
                return false;
            }

            FreezeManager.unfreezePlayer(target);
            player.sendMessage(Lang.getPrefix("Admin") + "<gray>You have unfrozen <red>" + target.getName());
            return true;
        }

        return false;
    }
}
