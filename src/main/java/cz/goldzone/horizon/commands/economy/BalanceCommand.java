package cz.goldzone.horizon.commands.economy;

import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BalanceCommand implements CommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(BalanceCommand.class.getName());

    public BalanceCommand() {
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

        if (args.length == 0) {
            showBalance(player);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return new SetBalanceCommand().onCommand(sender, command, label, args);
        }

        OfflinePlayer target = findPlayerByName(args[0]);
        if (target == null) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player not found!");
            return true;
        }

        double targetBalance = EconomyManager.getBalance(target);
        player.sendMessage(Lang.getPrefix("Horizon") + "<red>" + target.getName() + "<gray> has <red>$" + targetBalance + "<gray> in their account.");
        return true;
    }

    private void showBalance(Player player) {
        double balance = EconomyManager.getBalance(player);
        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Your balance: <red>$" + balance);
    }

    private OfflinePlayer findPlayerByName(String playerName) {
        OfflinePlayer target = Bukkit.getPlayerExact(playerName);
        if (target == null) {
            UUID targetUUID = getUUIDFromDatabase(playerName);
            if (targetUUID != null) {
                target = Bukkit.getOfflinePlayer(targetUUID);
            }
        }
        return target;
    }

    private UUID getUUIDFromDatabase(String playerName) {
        try (
                @Cleanup Connection connection = Core.getMySQL().getConnection();
                @Cleanup PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM players WHERE name = ?")
        ) {
            ps.setString(1, playerName);

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching UUID for player: " + playerName, e);
        }
        return null;
    }
}
