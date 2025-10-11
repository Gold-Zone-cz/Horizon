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

public class BalanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your balance: <red>" + EconomyManager.formatCurrency(EconomyManager.getBalance(player)));
            return true;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return new SetBalanceCommand().onCommand(sender, command, label, args);
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName() != null && p.getName().equalsIgnoreCase(targetName)) {
                    target = p;
                    break;
                }
            }
        }

        if (target == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found!");
            return true;
        }

        double targetBalance = EconomyManager.getBalance(target);
        player.sendMessage(Lang.getPrefix("Horizon") + "<red>" + target.getName() + "<gray> has <red>" + EconomyManager.formatCurrency(targetBalance));
        return true;
    }
}
