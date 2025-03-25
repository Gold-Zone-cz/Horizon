package cz.goldzone.horizon.managers;

import cz.goldzone.neuron.shared.Core;
import lombok.Cleanup;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MoneyManager {
    private final long playerID;

    public MoneyManager(Player player) {
        this.playerID = player.getUniqueId().getMostSignificantBits();
    }

    public static MoneyManager getMoneyAvailable(Player player) {
        return new MoneyManager(player);
    }

    public static void createBalanceTable() {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `survival_users_bank` (" +
                    "`user_id` BIGINT NOT NULL, " +
                    "`balance` BIGINT DEFAULT 0, " +
                    "`bank` BIGINT DEFAULT 0, " +
                    "PRIMARY KEY (`user_id`))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getAmount() {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement("SELECT `balance` FROM `survival_users_bank` WHERE `user_id` = ?");
            ps.setLong(1, playerID);
            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("balance");
            } else {
                createNewUser();
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setAmount(long balance) {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement("SELECT `balance` FROM `survival_users_bank` WHERE `user_id` = ?");
            ps.setLong(1, playerID);
            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                updateBalance(balance);
            } else {
                createNewUser(balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBalance(long balance) throws SQLException {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement("UPDATE `survival_users_bank` SET `balance` = ? WHERE `user_id` = ?");
            ps.setLong(1, balance);
            ps.setLong(2, playerID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewUser(long balance) throws SQLException {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement("INSERT INTO `survival_users_bank` (`user_id`, `balance`) VALUES (?, ?)");
            ps.setLong(1, playerID);
            ps.setLong(2, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewUser() throws SQLException {
        createNewUser(0);
    }

    public void add(int purseAdd) {
        long balance = getAmount();
        setAmount(balance + purseAdd);
    }

    public void subtract(int purseDelete) {
        long balance = getAmount();
        setAmount(balance - purseDelete);
    }

    public void reset() {
        setAmount(0);
    }
}