package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class WarpCommand implements CommandExecutor {
    private final ConfigManager configManager = Main.getConfigManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        FileConfiguration warpsConfig = configManager.getConfig("warps.yml");

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }


        teleportToWarp(player, warpsConfig, args[0]);
        return true;
    }

    private void sendHelpMessage(Player player) {
        if (player.hasPermission("horizon.admin.warp")) {
            player.sendMessage("<white>");
            player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available commands:");
            player.sendMessage("<white>");
            player.sendMessage("<dark_gray>【 <red>/setwarp <name> <dark_gray>】 <gray>Create a warp");
            player.sendMessage("<dark_gray>【 <red>/delwarp <name> <dark_gray>】 <gray>Delete a warp");
            player.sendMessage("<white>");
            player.sendMessage("<dark_gray>【 <red>/warp <name> <dark_gray>】 <gray>Teleport to a warp");
            player.sendMessage("<dark_gray>【 <red>/warps <dark_gray>】 <gray>Show all warps");
            player.sendMessage("<white>");
        } else {
            player.sendMessage("<white>");
            player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available commands:");
            player.sendMessage("<white>");
            player.sendMessage("<dark_gray>【 <red>/warp <name> <dark_gray>】 <gray>Teleport to a warp");
            player.sendMessage("<dark_gray>【 <red>/warps <dark_gray>】 <gray>Show all warps");
            player.sendMessage("<white>");
        }
    }

    private void teleportToWarp(Player player, FileConfiguration warpsConfig, String warpName) {

        if (!warpsConfig.contains(warpName)) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>No warp found with this name!");
            return;
        }

        Location warpLocation = (Location) warpsConfig.get(warpName + ".location");
        if (warpLocation == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp location is invalid!");
            return;
        }

        player.teleport(warpLocation);
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>You have been teleported to warp <red>" + warpName + "<gray>!");
    }
}
