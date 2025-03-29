package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FillTabManager implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {

            // Reload command
            if (command.getName().equalsIgnoreCase("horizon") && sender.hasPermission("horizon.admin.reload")) {
                suggestions.add("reload");
            }

            // Time vote command
            if (command.getName().equalsIgnoreCase("tv")) {
                suggestions.add("day");
                suggestions.add("night");
            }

            // Balance Command
            if (command.getName().equalsIgnoreCase("balance") && sender.hasPermission("horizon.admin.economy")) {
                suggestions.add("set");
            }

            // Player Warp command
            if (command.getName().equalsIgnoreCase("pw")) {
                suggestions.add("create");
                suggestions.add("delete");
                suggestions.add("list");
            }

            // Warp and delwarp commands
            if ((command.getName().equalsIgnoreCase("warp") || command.getName().equalsIgnoreCase("delwarp")) &&
                    sender.hasPermission("horizon.admin.warp")) {
                FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");
                if (warpsConfig != null) {
                    Set<String> warpKeys = warpsConfig.getKeys(false);
                    suggestions.addAll(warpKeys);
                }
            }

            // Home and delhome commands
            if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("delhome")) {
                if (sender instanceof Player player) {
                    List<String> homes = HomesManager.getHomes(player);
                    suggestions.addAll(homes);
                }
            }

            // Freeze and unfreeze commands
            if (command.getName().equalsIgnoreCase("freeze") && sender.hasPermission("horizon.admin.freeze")) {
                for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
                    if (!onlinePlayer.hasPermission("horizon.admin.freeze")) {
                        suggestions.add(onlinePlayer.getName());
                    }
                }
            }

            if (command.getName().equalsIgnoreCase("unfreeze") && sender.hasPermission("horizon.admin.freeze")) {
                for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
                    if (!onlinePlayer.hasPermission("horizon.admin.freeze")) {
                        suggestions.add(onlinePlayer.getName());
                    }
                }
            }
        }
        // Pay command
        if (args.length == 2 && command.getName().equalsIgnoreCase("pay")) {
            for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
                if (!onlinePlayer.getName().equalsIgnoreCase(sender.getName())) {
                    suggestions.add(onlinePlayer.getName());
                }
            }
        }

        if (args.length == 3 && command.getName().equalsIgnoreCase("pay")) {
            suggestions.add("<amount>");
        }

        // Tpa command
        if (args.length == 2 && command.getName().equalsIgnoreCase("tpa")) {
            for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
                if (!onlinePlayer.getName().equalsIgnoreCase(sender.getName())) {
                    suggestions.add(onlinePlayer.getName());
                }
            }
        }

        // Balance logic
        if (args.length == 2 && command.getName().equalsIgnoreCase("balance") && args[0].equalsIgnoreCase("set")) {
            for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
                suggestions.add(onlinePlayer.getName());
            }
        }

        if (args.length == 3 && command.getName().equalsIgnoreCase("balance") && args[0].equalsIgnoreCase("set")) {
            suggestions.add("<amount>");
        }

        return suggestions.isEmpty() ? null : suggestions;
    }
}

