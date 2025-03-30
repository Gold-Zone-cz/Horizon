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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BalTopCommand implements CommandExecutor {

    public BalTopCommand() {
        if (EconomyManager.hasEconomy()) {
            Bukkit.getLogger().warning("Vault or Economy plugin not found!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        List<Map.Entry<String, Double>> topPlayers = getTopPlayers();

        sender.sendMessage("\n<red>----- <gray>Baltop <red>-----");
        int rank = 1;
        for (Map.Entry<String, Double> entry : topPlayers) {
            sender.sendMessage("<red>" + rank + ". <gray>" + entry.getKey() + " <red>$" + EconomyManager.format(entry.getValue()));
            rank++;
        }
        return true;
    }

    private List<Map.Entry<String, Double>> getTopPlayers() {
        List<Map.Entry<String, Double>> sortedPlayers = new ArrayList<>();

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            double balance = EconomyManager.getBalance(player);
            sortedPlayers.add(new AbstractMap.SimpleEntry<>(player.getName(), balance));
        }

        sortedPlayers.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        return sortedPlayers.subList(0, Math.min(10, sortedPlayers.size()));
    }
}
