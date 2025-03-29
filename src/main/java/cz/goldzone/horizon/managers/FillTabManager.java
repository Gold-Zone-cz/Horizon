package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import org.bukkit.Material;
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
            if (command.getName().equalsIgnoreCase("timevote")) {
                suggestions.add("day");
                suggestions.add("night");
            }

            // Balance Command
            if (command.getName().equalsIgnoreCase("balance") && sender.hasPermission("horizon.admin.economy")) {
                suggestions.add("set");
            }

            // Player Warp command
            if (command.getName().equalsIgnoreCase("pwarp")) {
                suggestions.add("create");
                suggestions.add("delete");
                suggestions.add("list");
            }

            // Item command
            if (command.getName().equalsIgnoreCase("i") || sender.hasPermission("horizon.admin.economy")) {
                for (Material material : Material.values()) {
                    if (material.isItem() && material != Material.AIR && material.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        suggestions.add(material.name().toLowerCase());
                    }
                }
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
        return suggestions.isEmpty() ? null : suggestions;
    }
}

