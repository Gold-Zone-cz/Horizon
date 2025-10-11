package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetBalanceCommand implements CommandExecutor {

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

        OfflinePlayer target = getOfflinePlayerByName(args[1]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found!");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Lang.getPrefix("Economy") + "<red>Invalid amount!");
            return false;
        }

        EconomyManager.setBalance(target, amount);
        sender.sendMessage(Lang.getPrefix("Economy") + "<gray>You set <red>" + target.getName() + " <gray>balance to <red>" + EconomyManager.formatCurrency(amount));
        if (target.isOnline()) {
            Objects.requireNonNull(target.getPlayer()).sendMessage(Lang.getPrefix("Economy") + "<gray>Your balance was set to <red>" + EconomyManager.formatCurrency(amount) + "<gray> by an admin.");
        }

        return true;
    }

    private OfflinePlayer getOfflinePlayerByName(String name) {
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
