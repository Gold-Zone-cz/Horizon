package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {

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

        String targetName = args[0];
        String amountRaw = args[1];

        new BukkitRunnable() {
            @Override
            public void run() {
                OfflinePlayer target = getOfflinePlayerByName(targetName);
                if (target == null || !target.hasPlayedBefore()) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found!");
                }

                if (target == null) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Player not found!");
                    return;
                }

                if (!EconomyManager.isPayEnabled(target)) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Player " + target.getName() + " has disabled payments.");
                }


                if (!EconomyManager.isPayEnabled(target)) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Player " + target.getName() + " has disabled payments.");
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountRaw);
                } catch (NumberFormatException e) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Invalid amount!");
                    return;
                }

                if (amount <= 0) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Amount must be greater than 0!");
                    return;
                }

                if (player.getUniqueId().equals(target.getUniqueId())) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>You cannot pay yourself!");
                    return;
                }

                if (!EconomyManager.hasEnough(player, amount)) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>You don't have enough money!");
                    return;
                }

                if (!EconomyManager.isPayEnabled(target)) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>" + target.getName() + " has disabled receiving payments.");
                    return;
                }

                boolean successWithdraw = EconomyManager.withdraw(player, amount);
                boolean successDeposit = EconomyManager.deposit(target, amount);

                if (successWithdraw && successDeposit) {
                    player.sendMessage(Lang.getPrefix("Economy") + "<gray>You sent <red>" + EconomyManager.formatCurrency(amount) + "<gray> to <red>" + target.getName());
                    if (target.isOnline()) {
                        Player onlineTarget = target.getPlayer();
                        if (onlineTarget != null) {
                            onlineTarget.sendMessage(Lang.getPrefix("Economy") + "<red>" + player.getName() + "<gray> sent you <red>" + EconomyManager.formatCurrency(amount));
                        }
                    }
                } else {
                    player.sendMessage(Lang.getPrefix("Economy") + "<red>Transaction failed!");
                }
            }
        }.runTaskAsynchronously(Main.getInstance());

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
