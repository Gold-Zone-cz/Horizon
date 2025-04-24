package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        String warpName = args[0].toLowerCase();
        teleportToWarp(player, warpName);
        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("<white>");
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available commands:");

        if (player.hasPermission("horizon.admin.warp")) {
            player.sendMessage("<dark_gray>【 <red>/setwarp <name> <dark_gray>】 <gray>Create a warp");
            player.sendMessage("<dark_gray>【 <red>/delwarp <name> <dark_gray>】 <gray>Delete a warp");
        }

        player.sendMessage("<dark_gray>【 <red>/warp <name> <dark_gray>】 <gray>Teleport to a warp");
        player.sendMessage("<dark_gray>【 <red>/warps <dark_gray>】 <gray>Show all warps");
        player.sendMessage("<white>");
    }

    private void teleportToWarp(Player player, String warpName) {
        Configuration config = ConfigManager.getConfig("warps");
        ConfigurationSection configPath = config.getSection("Warps." + warpName);

        if (configPath == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>No warp found with the name: " + warpName + "!");
            return;
        }

        String worldName = configPath.getString(".location.world");
        if (worldName == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>World name is missing for warp " + warpName + "!");
            return;
        }

        World world = org.bukkit.Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>World " + worldName + " does not exist or is not loaded!");
            return;
        }

        Location location = configPath.get("location", Location.class);

        player.teleport(location);
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>You have been teleported to warp <red>" + warpName + "<gray>!");
    }
}