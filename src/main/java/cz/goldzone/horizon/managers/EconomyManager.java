package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.misc.EconomyHandler;
import cz.goldzone.neuron.shared.Core;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EconomyManager {
    @Getter
    private static Economy economy = null;
    // private static Permission permissions = null;
    // private static Chat chat = null;

    public static boolean setup(JavaPlugin plugin) {
        if (!setupEconomy(plugin)) {
            plugin.getLogger().severe("Vault or Economy plugin not found!");
            return false;
        }
        setupPermissions(plugin);
        setupChat(plugin);

        plugin.getLogger().info("Vault integration enabled.");
        return true;
    }

    private static final String TABLE = "survival_player_economy";

    public static void createTable() {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                             "uuid CHAR(36) NOT NULL PRIMARY KEY," +
                             "name VARCHAR(16) NOT NULL," +
                             "balance DOUBLE NOT NULL DEFAULT 0," +
                             "pay_enabled BOOLEAN NOT NULL DEFAULT TRUE)"
             )) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void register() {
        Bukkit.getServicesManager().register(net.milkbowl.vault.economy.Economy.class,
                new EconomyHandler(),
                Main.getInstance(),
                ServicePriority.Normal
        );
    }

    private static boolean setupEconomy(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private static void setupChat(JavaPlugin plugin) {
        // RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        // chat = rsp != null ? rsp.getProvider() : null;
    }

    private static void setupPermissions(JavaPlugin plugin) {
        // RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        // permissions = rsp != null ? rsp.getProvider() : null;
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    public static double getBalance(OfflinePlayer player) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT balance FROM " + TABLE + " WHERE uuid=?")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setBalance(OfflinePlayer player, double amount) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + TABLE + " (uuid,name,balance,pay_enabled) VALUES (?,?,?,?) " +
                             "ON DUPLICATE KEY UPDATE name=?, balance=?")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setDouble(3, amount);
            stmt.setBoolean(4, isPayEnabled(player));
            stmt.setString(5, player.getName());
            stmt.setDouble(6, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean withdraw(OfflinePlayer player, double amount) {
        double balance = getBalance(player);
        if (balance < amount) return false;
        setBalance(player, balance - amount);
        return true;
    }

    public static boolean deposit(OfflinePlayer player, double amount) {
        setBalance(player, getBalance(player) + amount);
        return true;
    }

    public static boolean isPayEnabled(OfflinePlayer player) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT pay_enabled FROM " + TABLE + " WHERE uuid=?")) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean("pay_enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void setPayEnabled(OfflinePlayer player, boolean enabled) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + TABLE + " (uuid,name,balance,pay_enabled) VALUES (?,?,?,?) " +
                             "ON DUPLICATE KEY UPDATE pay_enabled=?")) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setDouble(3, getBalance(player));
            stmt.setBoolean(4, enabled);
            stmt.setBoolean(5, enabled);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String formatCurrency(double amount) {
        return String.format("$%,.0f", amount);
    }
}


