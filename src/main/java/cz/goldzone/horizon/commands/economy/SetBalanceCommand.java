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

import java.util.Objects;

public class SetBalanceCommand implements CommandExecutor {

    public SetBalanceCommand() {
        if (!EconomyManager.hasEconomy()) {
            Bukkit.getLogger().warning("Vault or Economy plugin not found!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("horizon.admin.economy")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length < 3 || !args[0].equalsIgnoreCase("set")) {
            sender.sendMessage(Lang.getPrefix("Economy") + "<gray>Usage: <red>/balance set <player> <amount>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayerExact(args[1]);

        if (targetPlayer == null) {
            OfflinePlayer targetOffline = getOfflinePlayerByName(args[1]);
            if (targetOffline == null || !targetOffline.hasPlayedBefore()) {
                sender.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found or has never played before.");
                return false;
            }
            targetPlayer = (Player) targetOffline;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount < 0) {
                sender.sendMessage(Lang.getPrefix("Economy") + "<red>Amount cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Lang.getPrefix("Economy") + "<red>Invalid amount! Please enter a valid number.");
            return false;
        }

        EconomyManager.setBalance(targetPlayer, amount);

        sender.sendMessage(Lang.getPrefix("Economy") + "<gray>You have set <red>" + targetPlayer.getName() + " <gray>'s balance to <red>$" + amount);

        if (targetPlayer.isOnline()) {
            targetPlayer.sendMessage(Lang.getPrefix("Economy") + "<gray>Your balance has been updated to <red>$" + amount + "<gray> by an admin.");
        } else {
            Bukkit.getLogger().info("Target player " + targetPlayer.getName() + " is offline. Balance updated, but no message sent.");
        }

        return true;
    }

    private OfflinePlayer getOfflinePlayerByName(String playerName) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (Objects.requireNonNull(player.getName()).equalsIgnoreCase(playerName)) {
                return player;
            }
        }
        return null;
    }
}