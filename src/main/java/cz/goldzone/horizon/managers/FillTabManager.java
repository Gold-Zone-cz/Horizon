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
            handleFirstArgumentSuggestions(sender, command, suggestions);
        } else if (args.length == 2) {
            handleSecondArgumentSuggestions(sender, command, suggestions, args);
        } else if (args.length == 3) {
            handleThirdArgumentSuggestions(command, suggestions, args);
        }

        return suggestions.isEmpty() ? List.of() : suggestions;
    }

    private void handleFirstArgumentSuggestions(CommandSender sender, Command command, List<String> suggestions) {
        String commandName = command.getName().toLowerCase();

        switch (commandName) {
            case "horizon":
                if (sender.hasPermission("horizon.admin.reload")) {
                    suggestions.add("reload");
                }
                break;

            case "tv":
                suggestions.add("day");
                suggestions.add("night");
                break;

            case "balance":
                if (sender.hasPermission("horizon.admin.economy")) {
                    suggestions.add("set");
                }
                break;

            case "pw":
                suggestions.add("create");
                suggestions.add("delete");
                suggestions.add("list");
                break;

            case "warp":
            case "delwarp":
                if (sender.hasPermission("horizon.admin.warp")) {
                    addWarpSuggestions(suggestions);
                }
                break;

            case "home":
            case "delhome":
                if (sender instanceof Player player) {
                    List<String> homes = HomesManager.getHomes(player);
                    suggestions.addAll(homes);
                }
                break;

            case "freeze":
            case "unfreeze":
                addOnlinePlayersWithoutPermission(suggestions);
                break;
        }
    }

    private void addWarpSuggestions(List<String> suggestions) {
        FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");
        if (warpsConfig != null) {
            Set<String> warpKeys = warpsConfig.getKeys(false);
            suggestions.addAll(warpKeys);
        }
    }

    private void handleSecondArgumentSuggestions(CommandSender sender, Command command, List<String> suggestions, String[] args) {
        String commandName = command.getName().toLowerCase();

        if ("pay".equalsIgnoreCase(commandName) || "tpa".equalsIgnoreCase(commandName)) {
            addOnlinePlayersExcludingSender(suggestions, sender);
        }

        if ("balance".equalsIgnoreCase(commandName)) {
            if (sender.hasPermission("horizon.admin.economy")) {
                suggestions.add("set");
            }
            if (args.length > 0 && "set".equalsIgnoreCase(args[0])) {
                addOnlinePlayersExcludingSender(suggestions, sender);
            }
        }
    }

    private void handleThirdArgumentSuggestions(Command command, List<String> suggestions, String[] args) {
        String commandName = command.getName().toLowerCase();

        if ("pay".equalsIgnoreCase(commandName) || ("balance".equalsIgnoreCase(commandName) && args.length > 0 && "set".equalsIgnoreCase(args[0]))) {
            suggestions.add("<amount>");
        }
    }

    private void addOnlinePlayersWithoutPermission(List<String> suggestions) {
        for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission("horizon.admin.freeze")) {
                suggestions.add(onlinePlayer.getName());
            }
        }
    }

    private void addOnlinePlayersExcludingSender(List<String> suggestions, CommandSender sender) {
        String senderName = sender.getName();
        for (Player onlinePlayer : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!onlinePlayer.getName().equalsIgnoreCase(senderName)) {
                suggestions.add(onlinePlayer.getName());
            }
        }
    }
}