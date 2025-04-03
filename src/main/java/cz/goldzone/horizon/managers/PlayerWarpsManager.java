package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class PlayerWarpsManager {

    private static final String TABLE_NAME = "survival_playerwarps";
    public static Map<String, String> rateMessages = new HashMap<>();

    public static void createPlayerWarpTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "name VARCHAR(64) PRIMARY KEY,"
                + "user_id BIGINT NOT NULL,"
                + "category VARCHAR(32),"
                + "x DOUBLE, y DOUBLE, z DOUBLE,"
                + "world VARCHAR(64),"
                + "visit_count INT DEFAULT 0,"
                + "rating INT DEFAULT 0,"
                + "rate_message TEXT DEFAULT NULL,"
                + "rate_threshold INT DEFAULT 3)";
        executeUpdate(sql);
    }

    public static void setRateMessage(String warpName, String message, int threshold) {
        rateMessages.put(warpName, message);

        String sql = "UPDATE " + TABLE_NAME + " SET rate_message = ?, rate_threshold = ? WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, message);
            ps.setInt(2, threshold);
            ps.setString(3, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error setting rate message and threshold: ", e);
        }
    }

    public static int getRateThreshold(String warpName) {
        String sql = "SELECT rate_threshold FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rate_threshold");
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp rate threshold: ", e);
        }
        return 3;
    }

    public static String getRateMessage(String warpName) {
        String sql = "SELECT rate_message FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rate_message");
                }
            }
        } catch (SQLException e) {
            logError("Error getting rate message: ", e);
        }
        return "No rate message has been set.";
    }

    public static void createPlayerWarp(Player player, String warpName, Category category) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "INSERT INTO " + TABLE_NAME + " (name, user_id, category, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());
            ps.setString(3, category.name());
            Location loc = player.getLocation();
            ps.setDouble(4, loc.getX());
            ps.setDouble(5, loc.getY());
            ps.setDouble(6, loc.getZ());
            ps.setString(7, Objects.requireNonNull(loc.getWorld()).getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error creating player warp: ", e);
        }
    }

    public static void deletePlayerWarp(Player player, String name) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.toLowerCase());
            ps.setLong(2, gamePlayer.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error deleting player warp: ", e);
        }
    }

    public static String getMostVisitedWarp() {
        String sql = "SELECT name FROM " + TABLE_NAME + " ORDER BY visit_count DESC LIMIT 1";

        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
               return rs.getString("name");
            }
        } catch (SQLException e) {
            logError("Error getting most visited warp. Query: " + sql, e);
        }
        return "None";
    }

    public static int getPlayerWarpRating(String warpName) {
        String sql = "SELECT rating FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating");
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp rating: ", e);
        }
        return 0;
    }

    public static int getPlayerWarpVisitCount(String warpName) {
        String sql = "SELECT visit_count FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("visit_count");
                }
            }
        } catch (SQLException e) {
            logError("Error getting visit count: ", e);
        }
        return 0;
    }

    public static String getPlayerWarpOwner(String warpName) {
        String sql = "SELECT user_id FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    return getPlayerNameFromUserId(userId);
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp owner: ", e);
        }
        return "Unknown";
    }

    public static String getPlayerNameFromUserId(long userId) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(userId);
        if (gamePlayer != null) {
            return gamePlayer.getName();
        }
        return "Unknown";
    }

    public static List<String> getPlayerWarps(Player player) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);

        List<String> warpNames = new ArrayList<>();
        String sql = "SELECT name FROM " + TABLE_NAME + " WHERE user_id = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, gamePlayer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    warpNames.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            logError("Error getting player warps: ", e);
        }
        return warpNames;
    }

    public static Category getPlayerWarpCategory(String warpName) {
        String sql = "SELECT category FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Category.valueOf(rs.getString("category"));
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp category: ", e);
        }
        return Category.MISC;
    }

    public static void setPlayerWarpRating(String warpName, int rating) {
        String sql = "UPDATE " + TABLE_NAME + " SET rating = ? WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error setting warp rating: ", e);
        }
    }

    public static boolean hasVisitedPlayerWarp(Player player, String warpName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);

        String sql = "SELECT visit_count FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logError("Error checking if player has visited warp: ", e);
        }
        return false;
    }

    public static boolean hasRatedPlayerWarp(Player player, String warpName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);

        String sql = "SELECT rating FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int rating = rs.getInt("rating");
                    return rating != 0;
                }
            }
        } catch (SQLException e) {
            logError("Error checking if player has rated warp: ", e);
        }
        return false;
    }

    public static Optional<Location> getPlayerWarpLocation(String warpName) {
        String sql = "SELECT x, y, z, world FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    String world = rs.getString("world");
                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                    return Optional.of(loc);
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp location: ", e);
        }
        return Optional.empty();
    }

    public static boolean teleportToPlayerWarp(Player player, String warpName) {
        Optional<Location> warpLocation = getPlayerWarpLocation(warpName);

        if (warpLocation.isPresent()) {
            player.teleport(warpLocation.get());
            incrementVisitCount(warpName);
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been teleported to player warp <red>" + warpName);
        } else {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player warp with this name does not exist!");
        }
        return warpLocation.isPresent();
    }

    public static void incrementVisitCount(String warpName) {
        String sql = "UPDATE " + TABLE_NAME + " SET visit_count = visit_count + 1 WHERE name = ?";
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error incrementing visit count: ", e);
        }
    }

    private static void executeUpdate(String sql) {
        try (Connection connection = Core.getPluginMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("SQL Update Error: ", e);
        }
    }

    private static void logError(String message, Exception e) {
        Main.getInstance().getLogger().log(Level.SEVERE, message, e);
    }
}
