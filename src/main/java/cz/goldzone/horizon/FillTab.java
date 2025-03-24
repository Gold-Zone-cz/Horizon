package cz.goldzone.horizon;

import cz.goldzone.horizon.managers.HomesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FillTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (command.getName().equalsIgnoreCase("horizon") && sender.hasPermission("horizon.admin.reload")) {
                return List.of("reload");
            }
            if (command.getName().equalsIgnoreCase("pwarp")) {
                return List.of("create", "delete", "list");
            }
            if (command.getName().equalsIgnoreCase("warp") ||
                    command.getName().equalsIgnoreCase("delwarp")
                            && sender.hasPermission("horizon.warp.admin")) {
                FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");
                if (warpsConfig != null) {
                    Set<String> warpKeys = warpsConfig.getKeys(false);
                    suggestions.addAll(warpKeys);
                }
            }
            if (command.getName().equalsIgnoreCase("home") ||
                    command.getName().equalsIgnoreCase("delhome")) {
                if (sender instanceof Player player) {
                    List<String> homes = HomesManager.getHomes(player);
                    suggestions.addAll(homes);
                }
            }
        }
        return suggestions.isEmpty() ? null : suggestions;
    }
}