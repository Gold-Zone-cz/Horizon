package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {

    public PayCommand() {
        if (EconomyManager.hasEconomy()) {
            Bukkit.getLogger().warning("Vault or Economy plugin not found!");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Lang.getPrefix("Economy") + "<gray>Usage: <red>/pay <player> <amount>");
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found or offline!");
            return false;
        }

        if (player.equals(target)) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>You can't pay yourself!");
            return false;
        }

        if (!PayToggleCommand.isPayEnabled(target)) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>The player has disabled payments.");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage(Lang.getPrefix("Economy") + "<red>Amount must be greater than 0!");
                return false;
            }
            if (amount > 1_000_000) {
                player.sendMessage(Lang.getPrefix("Economy") + "<red>Maximum transaction limit is 1,000,000!");
                return false;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>Invalid amount!");
            return false;
        }

        if (!EconomyManager.hasEnough(player, amount)) {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>You don't have enough money!");
            return false;
        }

        if (EconomyManager.withdraw(player, amount) && EconomyManager.deposit(target, amount)) {
            player.sendMessage(Lang.getPrefix("Economy") + "<gray>You sent <red>$" + amount + " <gray>to <red>" + target.getName());
            target.sendMessage(Lang.getPrefix("Economy") + "<red>" + player.getName() + "<gray> sent you <red>$" + amount);
        } else {
            player.sendMessage(Lang.getPrefix("Economy") + "<red>Transaction failed!");
        }

        return true;
    }
}