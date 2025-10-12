package cz.goldzone.horizon.managers;

import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.player.GamePlayer;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomesManager {

    private static final String TABLE_NAME = "survival_player_homes";

    public static void createTable() {
        String sql = Lang.format(
                "CREATE TABLE IF NOT EXISTS %{1} (" +
                        "`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE," +
                        "user_id BIGINT NOT NULL," +
                        "home_name VARCHAR(64)," +
                        "x DOUBLE," +
                        "y DOUBLE," +
                        "z DOUBLE," +
                        "world VARCHAR(64)," +
                        "PRIMARY KEY (id, user_id, home_name))", TABLE_NAME);

        executeUpdate(sql);
    }

    public static void setHome(Player player, String homeName, Location location) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "REPLACE INTO survival_player_homes (user_id, home_name, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, gamePlayer.getId());
            ps.setString(2, homeName.toLowerCase());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setString(6, Objects.requireNonNull(location.getWorld()).getName());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteHome(Player player, String homeName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE user_id = ? AND home_name = ?";

        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, gamePlayer.getId());
            ps.setString(2, homeName.toLowerCase());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getHome(Player player, String homeName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ? AND home_name = ?";

        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, gamePlayer.getId());
            ps.setString(2, homeName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractLocation(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getHomes(Player player) {
        List<String> homes = new ArrayList<>();
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "SELECT home_name FROM " + TABLE_NAME + " WHERE user_id = ?";

        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, gamePlayer.getId());
            @Cleanup ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                homes.add(rs.getString("home_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return homes;
    }

    public static Location getHomeLocation(Player player, String homeName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "SELECT x, y, z, world FROM " + TABLE_NAME + " WHERE user_id = ? AND home_name = ?";

        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, gamePlayer.getId());
            ps.setString(2, homeName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractLocation(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Location extractLocation(ResultSet rs) throws Exception {
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        double z = rs.getDouble("z");
        String worldName = rs.getString("world");

        org.bukkit.World world = Bukkit.getWorld(worldName);
        return (world != null) ? new Location(world, x, y, z) : null;
    }

    private static void executeUpdate(String sql) {
        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
