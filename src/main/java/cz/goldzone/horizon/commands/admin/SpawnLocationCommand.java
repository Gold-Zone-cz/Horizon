package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SpawnLocationCommand implements CommandExecutor {

    private static final Set<Player> confirmQueue = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        Configuration config = ConfigManager.getConfig("config");
        Location oldSpawn = config.get("spawn.location", Location.class);


        if (oldSpawn != null && !confirmQueue.contains(player)) {
            confirmQueue.add(player);
            player.sendMessage(Lang.getPrefix("Horizon") +
                    "<yellow>A spawn location already exists.</yellow> " +
                    "<gray>Type <green>/setspawnlocation</green> again to confirm overwrite.");
            return true;
        }

        config.set("spawn.location", player.getLocation());
        config.save();

        confirmQueue.remove(player);

        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Spawn location successfully set to your current position.");
        return true;
    }
}