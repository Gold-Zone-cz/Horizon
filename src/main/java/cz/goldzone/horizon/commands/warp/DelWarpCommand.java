package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelWarpCommand implements CommandExecutor {

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
            player.sendMessage(Lang.getPrefix("Warps") + "<red>You must provide a warp name!");
            return false;
        }

        String name = args[0].toLowerCase();

        if (!name.matches("^[a-zA-Z0-9_]+$")) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp name can only contain letters, numbers and underscores.");
            return false;
        }

        Configuration config = ConfigManager.getConfig("warps");

        if (config.getSection("Warps." + name) == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp <gray>" + name + " <red>does not exist!");
            return false;
        }

        boolean confirmed = args.length >= 2 && (args[1].equalsIgnoreCase("--confirm") || args[1].equalsIgnoreCase("-c"));

        if (!confirmed) {
            player.sendMessage(Lang.getPrefix("Warps") + "<yellow>Warp <gray>" + name + " <yellow>exists. To delete it, use <gray>/delwarp " + name + " --confirm");
            return true;
        }

        config.set("Warps." + name, null);
        config.save();

        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Warp <red>" + name + " <gray>has been permanently deleted.");
        return true;
    }
}
