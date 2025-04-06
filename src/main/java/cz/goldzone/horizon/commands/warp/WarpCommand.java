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

        ConfigurationSection warpConfig = config.getSection("Warps." + warpName);

        if (warpConfig == null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>No warp found with the name: " + warpName + "!");
            return;
        }

        String worldName = warpConfig.getString("location.world");
        double x = warpConfig.getDouble("location.x");
        double y = warpConfig.getDouble("location.y");
        double z = warpConfig.getDouble("location.z");
        float yaw = (float) warpConfig.getDouble("location.yaw");
        float pitch = (float) warpConfig.getDouble("location.pitch");

        World world = player.getServer().getWorld(worldName);

        Location warpLocation = new Location(world, x, y, z, yaw, pitch);

        player.teleport(warpLocation);
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>You have been teleported to warp <red>" + warpName + "<gray>!");
    }
}
