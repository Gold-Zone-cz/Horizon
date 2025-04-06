package cz.goldzone.horizon.managers;

import cz.goldzone.neuron.shared.Core;
import lombok.Cleanup;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EconomyManager {
    private static Economy economy;

    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    public static boolean hasEconomy() {
        return economy != null;
    }

    public static void createBalanceTable() {
        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `survival_users_bank` (" +
                            "`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE," +
                            "`user_id` BIGINT NOT NULL, " +
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
//        if (hasEconomy()) return;
//        double currentBalance = getBalance(player);
//        if (balance > currentBalance) {
//            economy.depositPlayer(player, balance - currentBalance);
//        } else {
//            economy.withdrawPlayer(player, currentBalance - balance);
//        }
//        updateDatabaseBalance(player.getUniqueId(), balance);
    }

    public static boolean deposit(OfflinePlayer player, double amount) {
//        if (hasEconomy()) return false;
//        boolean success = economy.depositPlayer(player, amount).transactionSuccess();
//        if (success) updateDatabaseBalance(player.getUniqueId(), getBalance(player));
//        return success;
        return true;
    }

    public static boolean withdraw(Player gamePlayer, double amount) {
//        if (hasEconomy()) return false;
//        boolean success = economy.withdrawPlayer(player, amount).transactionSuccess();
//        if (success) updateDatabaseBalance(player.getUniqueId(), getBalance(player));
//        return success;
        return true;
    }

    private static void updateDatabaseBalance(Player gamePlayer, double balance) {
//        try {
//            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
//            @Cleanup PreparedStatement ps = connection.prepareStatement(
//                    "INSERT INTO `survival_users_bank` (`user_id`, `balance`) VALUES (?, ?) " +
//                            "ON DUPLICATE KEY UPDATE `balance` = ?");
//            ps.setLong(1, gamePlayer.getId());
//            ps.setDouble(2, balance);
//            ps.setDouble(3, balance);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {
        if (hasEconomy()) return false;
        double currentBalance = getBalance(player);
        return currentBalance >= amount;
    }

    // public static String format(double amount) {
    //     return String.format("%.2f", amount);
    // }
}
