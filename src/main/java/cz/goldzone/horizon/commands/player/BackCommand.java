package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.managers.BackCommandManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.player.back")) {
            player.sendMessage(Lang.get("core.no_perm", player));
            return false;
        }

        Location backLoc = BackCommandManager.getLastLocation(player);

        if (backLoc == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>No last location found.");
            return false;
        }

        player.teleport(backLoc);
        BackCommandManager.clear(player);
        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Teleported to your last location.");
        return true;
    }
}
