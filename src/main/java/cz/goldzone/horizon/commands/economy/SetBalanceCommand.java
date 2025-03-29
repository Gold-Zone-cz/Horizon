package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.MoneyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found!");
            return false;
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

        MoneyManager moneyManager = new MoneyManager(target);
        moneyManager.setAmount((long) amount);

        sender.sendMessage(Lang.getPrefix("Economy") + "<gray>You have set <red>" + target.getName() + " <gray>balance to <red>$" + amount);
        return true;
    }
}
