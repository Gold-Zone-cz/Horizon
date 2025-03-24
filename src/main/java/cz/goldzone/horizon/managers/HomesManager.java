package cz.goldzone.horizon.managers;

import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesManager {

    public static void createHomesTable() {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS survival_player_homes (" +
                            "uuid VARCHAR(36)," +
                            "home_name VARCHAR(64)," +
                            "creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "x DOUBLE," +
                            "y DOUBLE," +
                            "z DOUBLE," +
                            "world VARCHAR(64)," +
                            "PRIMARY KEY (uuid, home_name))"
            );
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setHome(Player player, String homeName, Location location) {
        UUID playerId = player.getUniqueId();

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "REPLACE INTO survival_player_homes (uuid, home_name, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, playerId.toString());
            ps.setString(2, homeName.toLowerCase());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setString(6, location.getWorld().getName());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM survival_player_homes WHERE uuid = ? AND home_name = ?");
            ps.setString(1, playerId.toString());
            ps.setString(2, homeName.toLowerCase());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM survival_player_homes WHERE uuid = ? AND home_name = ?");
            ps.setString(1, playerId.toString());
            ps.setString(2, homeName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                String worldName = rs.getString("world");

                org.bukkit.World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    player.sendMessage(Lang.getPrefix("Homes") + "<red>World " + worldName + " does not exist.");
                    return null;
                }

                return new Location(world, x, y, z);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> getHomes(Player player) {
        List<String> homes = new ArrayList<>();
        UUID playerId = player.getUniqueId();

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT home_name FROM survival_player_homes WHERE uuid = ?");
            ps.setString(1, playerId.toString());

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
        UUID playerId = player.getUniqueId();
        Location location = null;

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT x, y, z, world FROM survival_player_homes WHERE uuid = ? AND home_name = ?");
            ps.setString(1, playerId.toString());
            ps.setString(2, homeName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                String worldName = rs.getString("world");

                location = new Location(Bukkit.getWorld(worldName), x, y, z);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public static String getHomeCreationDate(String homeName, Player player) {
        UUID playerId = player.getUniqueId();

        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT creation_date FROM survival_player_homes WHERE uuid = ? AND home_name = ?");
            ps.setString(1, playerId.toString());
            ps.setString(2, homeName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("creation_date");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
