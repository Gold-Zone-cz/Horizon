package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FillTabManager implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 1 -> suggestFirstArg(sender, command, suggestions);
            case 2 -> suggestSecondArg(sender, command, suggestions, args);
            case 3 -> suggestThirdArg(command, suggestions, args);
        }

        return suggestions.isEmpty() ? List.of() : suggestions;
    }

    private void suggestFirstArg(CommandSender sender, Command command, List<String> suggestions) {
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "horizon" -> {
                if (sender.hasPermission("horizon.admin.reload")) suggestions.add("reload");
            }
            case "jail" -> {
                Configuration config = ConfigManager.getConfig("jail");
                ConfigurationSection jailLocation = config.getSection("JailPlace");
                if (sender instanceof Player player && player.hasPermission("horizon.admin.jail") && jailLocation != null) {
                    suggestOnlinePlayersExcept(sender, suggestions);
                }
            }
            case "playerwarps" -> suggestions.addAll(List.of("create", "delete", "list"));
            case "tv" -> suggestions.addAll(List.of("day", "night"));
            case "balance" -> {
                if (sender.hasPermission("horizon.admin.economy")) suggestions.add("set");
            }
            case "warp", "delwarp" -> {
                if (command.getName().equalsIgnoreCase("delwarp")) {
                    if (sender instanceof Player player && player.hasPermission("horizon.admin.warp")) {
                        suggestWarpNames(suggestions);
                    }
                } else {
                    suggestWarpNames(suggestions);
                }
            }
            case "home", "delhome" -> {
                if (sender instanceof Player player) {
                    suggestions.addAll(HomesManager.getHomes(player));
                }
            }
            case "freeze", "unfreeze" -> suggestOnlinePlayersNoPermission(suggestions);
        }
    }

    private void suggestSecondArg(CommandSender sender, Command command, List<String> suggestions, String[] args) {
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "pay", "tpa" -> suggestOnlinePlayersExcept(sender, suggestions);
            case "invsee" -> {
                if (sender.hasPermission("horizon.admin.invsee")) {
                    suggestOnlinePlayersExcept(sender, suggestions);
                }
            }
            case "balance" -> {
                if (sender.hasPermission("horizon.admin.economy")) suggestions.add("set");

                if ("set".equalsIgnoreCase(args[0])) {
                    suggestOnlinePlayersExcept(sender, suggestions);
                }
            }
        }
    }

    private void suggestThirdArg(Command command, List<String> suggestions, String[] args) {
        String cmd = command.getName().toLowerCase();

        if ("pay".equals(cmd) || ("balance".equals(cmd) && "set".equalsIgnoreCase(args[0]))) {
            suggestions.add("<amount>");
        }
    }

    private void suggestWarpNames(List<String> suggestions) {
        Configuration config = ConfigManager.getConfig("warps");
        List<String> keys = config.getKeys();
        if (keys != null) suggestions.addAll(keys);
    }

    private void suggestOnlinePlayersNoPermission(List<String> suggestions) {
        for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!player.hasPermission("horizon.admin.freeze")) {
                suggestions.add(player.getName());
            }
        }
    }

    private void suggestOnlinePlayersExcept(CommandSender sender, List<String> suggestions) {
        String senderName = sender.getName();
        for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
            if (!player.getName().equalsIgnoreCase(senderName)) {
                suggestions.add(player.getName());
            }
        }
    }
}
