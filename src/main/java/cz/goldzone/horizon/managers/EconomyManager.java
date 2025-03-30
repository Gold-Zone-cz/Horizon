package cz.goldzone.horizon.managers;

import cz.goldzone.neuron.shared.Core;
import lombok.Cleanup;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class EconomyManager {
    @Getter
    private static Economy economy;

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    public static boolean hasEconomy() {
        return economy == null;
    }

    public static void createBalanceTable() {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `survival_users_bank` (" +
                            "`user_id` VARCHAR(36) NOT NULL, " +
                            "`balance` DOUBLE DEFAULT 0, " +
                            "PRIMARY KEY (`user_id`))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getBalance(OfflinePlayer player) {
        if (hasEconomy()) return 0;
        return economy.getBalance(player);
    }

    public static void setBalance(OfflinePlayer player, double balance) {
        if (hasEconomy()) return;
        double currentBalance = getBalance(player);
        if (balance > currentBalance) {
            economy.depositPlayer(player, balance - currentBalance);
        } else {
            economy.withdrawPlayer(player, currentBalance - balance);
        }
        updateDatabaseBalance(player.getUniqueId(), balance);
    }

    public static boolean deposit(OfflinePlayer player, double amount) {
        if (hasEconomy()) return false;
        boolean success = economy.depositPlayer(player, amount).transactionSuccess();
        if (success) updateDatabaseBalance(player.getUniqueId(), getBalance(player));
        return success;
    }

    public static boolean withdraw(OfflinePlayer player, double amount) {
        if (hasEconomy()) return false;
        boolean success = economy.withdrawPlayer(player, amount).transactionSuccess();
        if (success) updateDatabaseBalance(player.getUniqueId(), getBalance(player));
        return success;
    }

    private static void updateDatabaseBalance(UUID playerUUID, double balance) {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO `survival_users_bank` (`user_id`, `balance`) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE `balance` = ?");
            ps.setString(1, playerUUID.toString());
            ps.setDouble(2, balance);
            ps.setDouble(3, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {
        if (hasEconomy()) return false;
        double currentBalance = getBalance(player);
        return currentBalance >= amount;
    }

    public static String format(double amount) {
        return String.format("%.2f", amount);
    }
}
