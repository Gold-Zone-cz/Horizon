package cz.goldzone.horizon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FillTab implements TabCompleter {

    private final List<String> horizonArguments = List.of("reload");
    private final List<String> playerWarpsArguments = List.of("create", "delete", "list");


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            if (command.getName().equalsIgnoreCase("horizon") && sender.hasPermission("horizon.admin.reload")) {
                return horizonArguments;
            }
            if (command.getName().equalsIgnoreCase("pwarp")) {
                return playerWarpsArguments;
            }
            if (command.getName().equalsIgnoreCase("warp") && sender.hasPermission("horizon.warp.admin")) {
                FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");
                if (warpsConfig != null) {
                    Set<String> warpKeys = warpsConfig.getKeys(false);
                    suggestions.addAll(warpKeys);
                }
            }
        }

        return suggestions.isEmpty() ? null : suggestions;
    }
}