package cz.goldzone.horizon.commands.warp;

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

public class SetWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.warp")) {
            player.sendMessage(Lang.getPrefix("Warps") + Lang.format(Lang.get("core.no_perm", player)));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>You must provide a name!");
            return false;
        }

        Configuration config = ConfigManager.getConfig("warps");

        String name = args[0].toLowerCase();
        Location location = player.getLocation();

        if (location.getWorld() == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Failed to set warp: world is null.");
            return false;
        }

        if (!name.matches("^[a-zA-Z0-9_]+$")) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp name can only contain letters, numbers and underscores.");
            return false;
        }

        if (name.length() < 3 || name.length() > 16) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp name must be between 3 and 16 characters!");
            return false;
        }

        boolean force = args.length >= 2 && (args[1].equalsIgnoreCase("-f") || args[1].equalsIgnoreCase("--force"));

        if (config.getSection("Warps." + name) != null && !force) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp <gray>" + name + " <red>already exists! Use <gray>-f <red>to overwrite.");
            return false;
        }

        config.set("Warps." + name + ".location", location);
        config.set("Warps." + name + ".staff", player.getName());

        try {
            config.save();
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to save warp configuration: " + e.getMessage());
            return false;
        }

        if (force) {
            player.sendMessage(Lang.getPrefix("Warps") + "<gray>Warp <red>" + name + " <gray>has been <yellow>overwritten");
        } else {
            player.sendMessage(Lang.getPrefix("Warps") + "<gray>Warp <red>" + name + " <gray>has been created!");
        }

        return true;
    }
}
