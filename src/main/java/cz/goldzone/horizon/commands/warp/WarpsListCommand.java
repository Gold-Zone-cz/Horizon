package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WarpsListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");

        player.sendMessage("<white>");
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available warps:");
        player.sendMessage("<white>");

        Set<String> warpKeys = warpsConfig.getKeys(false);
        if (warpKeys.isEmpty()) {
            player.sendMessage("<#333333>【 <red>No warps available. <#333333>】");
        } else {
            for (String key : warpKeys) {
                player.sendMessage("<#333333>【 <red>" + key + " <#333333>】");
            }
        }

        player.sendMessage("<white>");
        return true;
    }
}