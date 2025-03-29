package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.MoneyManager;
import cz.goldzone.neuron.shared.Lang;
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
            long balance = MoneyManager.getMoneyAvailable(player).getAmount();
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your balance is <red>$" + balance);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            return new SetBalanceCommand().onCommand(sender, command, label, args);
        }

        if (args.length == 1) {
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found!");
                return false;
            }
            long targetBalance = MoneyManager.getMoneyAvailable(target).getAmount();
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>" + target.getName() + "<gray> has <red>$" + targetBalance + "<gray> in their account.");
            return true;
        }

        return false;
    }
}
