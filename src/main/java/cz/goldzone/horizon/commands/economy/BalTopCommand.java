package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BalTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        int page = 1;
        if (args.length > 0) {
            try {
                page = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                player.sendMessage("<red>✖ <gray>Invalid page number.");
                return true;
            }
        }

        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        List<Map.Entry<String, Double>> sortedPlayers = players.stream()
                .filter(p -> p.getName() != null)
                .map(p -> new AbstractMap.SimpleEntry<>(p.getName(), EconomyManager.getBalance(p)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) sortedPlayers.size() / 10);
        if (page > totalPages && totalPages != 0) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>✖ <gray>Page " + page + " does not exist. Max page: " + totalPages);
            return true;
        }

        int start = (page - 1) * 10;
        int end = Math.min(start + 10, sortedPlayers.size());
        List<Map.Entry<String, Double>> pageEntries = sortedPlayers.subList(start, end);

        player.sendMessage("<red>───── <gray>Top Balances <gray>(Page " + page + "<dark_gray>/ <gray>" + totalPages + ") <red>─────");
        int rank = start + 1;
        for (Map.Entry<String, Double> entry : pageEntries) {
            player.sendMessage("<red>" + rank + ". <gray>" + entry.getKey()
                    + " <dark_gray>─ <red>" + EconomyManager.formatCurrency(entry.getValue()));
            rank++;
        }
        player.sendMessage("<red>───────────────────────");

        return true;
    }
}
