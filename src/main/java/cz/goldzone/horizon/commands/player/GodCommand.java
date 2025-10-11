package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return false;
        }

        if (!player.hasPermission("horizon.player.god")) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>You don't have permission to use this command.");
            return false;
        }

        boolean isInvulnerable = player.isInvulnerable();
        player.setInvulnerable(!isInvulnerable);

        if(isInvulnerable) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You are no longer in <yellow><b>god</b> <gray>mode.");
            return false;
        } else {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You are now in <yellow><b>god</b> <gray>mode.");
        }
        return true;
    }
}
