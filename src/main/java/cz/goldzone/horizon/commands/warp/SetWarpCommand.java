package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetWarpCommand implements CommandExecutor {
    private final ConfigManager configManager = Main.getConfigManager();

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

        String name = args[0].toLowerCase();
        if (configManager.getConfig("warps.yml").get(name) != null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp with this name already exists!");
            return false;
        }

        configManager.getConfig("warps.yml").set(name + ".location", player.getLocation());
        configManager.saveConfig("warps.yml");

        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Warp <red>" + name + " <gray>has been created!");
        return true;
    }
}

