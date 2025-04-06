package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpsListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        Configuration config = ConfigManager.getConfig("warps");

        player.sendMessage("<white>");
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available warps:");
        player.sendMessage("<white>");

        ConfigurationSection warpSection = config.getSection("Warps");

        if (warpSection == null || warpSection.getKeys().isEmpty()) {
            player.sendMessage("<dark_gray>【 <red>No warps available. <dark_gray>】");
        } else {
            StringBuilder warpMessage = new StringBuilder();

            for (String warpName : warpSection.getKeys()) {
                String warpDisplayName = warpSection.getString(warpName + ".displayName");
                warpMessage.append("<dark_gray>【 <red>").append(warpDisplayName).append(" <dark_gray>】")
                        .append("\n");
            }

            player.sendMessage(warpMessage.toString());
        }

        player.sendMessage("<white>");
        return true;
    }
}