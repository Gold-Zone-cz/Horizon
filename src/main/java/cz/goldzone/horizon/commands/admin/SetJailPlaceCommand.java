package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.Main;
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
import java.util.UUID;

public class SetJailPlaceCommand implements CommandExecutor {
    private static final Set<UUID> confirmationSet = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.jail")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/setjail");
            return false;
        }

        Configuration config = ConfigManager.getConfig("jail");

        if (config.getSection("JailPlace") != null && !confirmationSet.contains(player.getUniqueId())) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Jail location already exists!\n <gray>Retype the command to overwrite it.");
            confirmationSet.add(player.getUniqueId());
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> confirmationSet.remove(player.getUniqueId()), 600L);
            return true;
        } else {
            confirmationSet.remove(player.getUniqueId());
        }

        Location location = player.getLocation();
        config.set("JailPlace", location);
        config.save();

        player.sendMessage(Lang.getPrefix("Jail") + "<gray>Jail location has been <red>successfully <gray>set!");
        return true;
    }
}
