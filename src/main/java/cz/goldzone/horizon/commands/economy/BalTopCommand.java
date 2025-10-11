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

        List<OfflinePlayer> players = Arrays.asList(Bukkit.getOfflinePlayers());
        List<Map.Entry<String, Double>> topPlayers = players.stream()
                .map(p -> new AbstractMap.SimpleEntry<>(p.getName(), EconomyManager.getBalance(p)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        player.sendMessage("<red>----- <gray>Top Balances <red>-----");
        int rank = 1;
        for (Map.Entry<String, Double> entry : topPlayers) {
            player.sendMessage("<red>" + rank + ". <gray>" + entry.getKey() + " <red>" + EconomyManager.formatCurrency(entry.getValue()));
            rank++;
        }

        return true;
    }
}
