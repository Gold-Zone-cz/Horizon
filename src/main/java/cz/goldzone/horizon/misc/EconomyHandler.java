package cz.goldzone.horizon.misc;

import cz.goldzone.neuron.shared.Core;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EconomyHandler extends AbstractEconomy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Horizon";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f %s", amount, (amount == 1 ? currencyNameSingular() : currencyNamePlural()));
    }

    @Override
    public String currencyNamePlural() {
        return "Money";
    }

    @Override
    public String currencyNameSingular() {
        return "Money";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return getAccount(playerName) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        Double balance = getAccount(playerName);
        return balance != null ? balance : 0;
    }

    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        if (amount <= 0)
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        if (!has(playerName, amount))
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");

        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE survival_player_economy SET balance = balance - ? WHERE name = ?"
             )) {
            ps.setDouble(1, amount);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }

        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(playerName, null, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        if (amount <= 0)
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");

        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO survival_player_economy (uuid, name, balance, paytoggle) " +
                             "VALUES (?, ?, ?, 1) ON DUPLICATE KEY UPDATE balance = balance + ?"
             )) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, playerName);
            ps.setDouble(3, amount);
            ps.setDouble(4, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, e.getMessage());
        }

        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, "");
    }

    private Double getAccount(String playerName) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT balance FROM survival_player_economy WHERE name = ?"
             )) {
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        try (Connection conn = Core.getMySQL().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO survival_player_economy (uuid, name, balance, paytoggle) VALUES (?, ?, 0, 1)"
             )) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, playerName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
}
