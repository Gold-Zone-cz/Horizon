package cz.goldzone.horizon.managers;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.horizon.gui.PlayerWarpsGUI;
import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.player.GamePlayer;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PlayerWarpsManager {

    private static final String TABLE_NAME = "survival_playerwarps";

    public static void createPlayerWarpTable() {
        String sql = Lang.format(
                "CREATE TABLE IF NOT EXISTS %{1} ("
                        + "id BIGINT NOT NULL AUTO_INCREMENT UNIQUE,"
                        + "name VARCHAR(64) PRIMARY KEY, "
                        + "user_id BIGINT NOT NULL, "
                        + "username VARCHAR(32) NOT NULL,"
                        + "category VARCHAR(32), "
                        + "x DOUBLE, y DOUBLE, z DOUBLE, "
                        + "world VARCHAR(64), "
                        + "visit_count INT DEFAULT 0, "
                        + "rating INT DEFAULT 0, "
                        + "rate_message TEXT DEFAULT NULL, "
                        + "rate_threshold INT DEFAULT 0"
                        + ")", TABLE_NAME);
        executeUpdate(sql);
    }

    private static final Map<String, ItemStack> warpItemCache = new ConcurrentHashMap<>();

    public static ItemStack getCachedPlayerWarpItem(String warpName, Category category) {
        return warpItemCache.computeIfAbsent(warpName, k -> {
            XMaterial material = PlayerWarpsGUI.getRandomMaterialForCategory(category);
            return material.parseItem();
        });
    }

    public static void setRateMessage(String warpName, String message, int threshold) {
        try {
            String sql = "UPDATE " + TABLE_NAME + " SET rate_message = ?, rate_threshold = ? WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, message);
            ps.setInt(2, threshold);
            ps.setString(3, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error setting rate message: " + e.getMessage());
        }
    }

    public static int getRateThreshold(String warpName) {
        try {
            String sql = "SELECT rate_threshold FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("rate_threshold");
            }
        } catch (SQLException e) {
            logError("Error getting player warp rate threshold: ", e);
        }

        return 3;
    }


    public static String getRateMessage(String warpName) {
        try {
            String sql = "SELECT rate_message FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("rate_message");
            }
        } catch (SQLException e) {
            logError("Error getting rate message: ", e);
        }
        return "No rate message has been set.";
    }

    public static void createPlayerWarp(Player player, String warpName, Category category) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        try {
            String sql = "INSERT INTO " + TABLE_NAME + " (name, user_id, username, category, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());
            ps.setString(3, player.getName());
            ps.setString(4, category.name());

            Location loc = player.getLocation();
            ps.setDouble(5, loc.getX());
            ps.setDouble(6, loc.getY());
            ps.setDouble(7, loc.getZ());
            ps.setString(8, Objects.requireNonNull(loc.getWorld()).getName());

            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error creating player warp: ", e);
        }
    }

    public static void deletePlayerWarp(Player player, String name) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);
        try {
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, name.toLowerCase());
            ps.setLong(2, gamePlayer.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error deleting player warp: ", e);
        }
    }

    public static String getMostVisitedWarp(Category category) {
        try {
            String sql = "SELECT name FROM " + TABLE_NAME + " WHERE category = ? ORDER BY visit_count DESC LIMIT 1";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, category.name());

            @Cleanup ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            logError("Error getting most visited warp for category " + category.name() + ": ", e);
        }

        return "None";
    }

    public static int getPlayerWarpRating(String warpName) {
        try {
            String sql = "SELECT rating FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
        } catch (SQLException e) {
            logError("Error getting warp rating: ", e);
        }

        return 0;
    }

    public static int getPlayerWarpVisitCount(String warpName) {
        try {
            String sql = "SELECT visit_count FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("visit_count");
            }
        } catch (SQLException e) {
            logError("Error getting visit count: ", e);
        }

        return 0;
    }

    public static String getPlayerWarpOwner(String warpName) {
        try {
            String sql = "SELECT username FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            logError("Error getting owner for player warp: " + warpName, e);
        }

        return "Unknown";
    }

    public static List<String> getAllPlayerWarps() {
        List<String> warpNames = new ArrayList<>();
        try {
            String sql = "SELECT name FROM " + TABLE_NAME;

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            @Cleanup ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                warpNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            logError("Error getting all player warps: ", e);
        }

        return warpNames;
    }

    public static Category getPlayerWarpCategory(String warpName) {
        try {
            String sql = "SELECT category FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());

            @Cleanup ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String categoryStr = rs.getString("category");
                if (categoryStr != null && !categoryStr.isEmpty()) {
                    return Category.valueOf(categoryStr);
                }
            }
        } catch (SQLException e) {
            logError("Error getting warp category for warp: " + warpName, e);
        } catch (IllegalArgumentException e) {
            logError("Invalid category found for warp: " + warpName, e);
        }

        return Category.MISC;
    }

    public static void setPlayerWarpRating(String warpName, int rating) {
        try {
            String sql = "UPDATE " + TABLE_NAME + " SET rating = ? WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, rating);
            ps.setString(2, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error setting warp rating: ", e);
        }
    }

    public static void markWarpAsVisited(Player player, String warpName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);

        try {
            String sql = "INSERT INTO " + TABLE_NAME + " (name, user_id, visit_count) VALUES (?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE visit_count = visit_count + 1";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());

            ps.executeUpdate();
            Main.getInstance().getLogger().info("Marked warp '" + warpName + "' as visited for player " + player.getName() + ".");
        } catch (SQLException e) {
            logError("Error marking warp as visited: ", e);
        }
    }

    public static boolean hasVisitedPlayerWarp(Player player, String warpName) { GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);


        try {
            String sql = "SELECT visit_count FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());

            @Cleanup ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int visitCount = rs.getInt("visit_count");
                Main.getInstance().getLogger().info("Player " + player.getName() + " has visited warp '" + warpName + "' " + visitCount + " times.");
                return visitCount > 0;
            } else {
                Main.getInstance().getLogger().info("No record found for player " + player.getName() + " and warp '" + warpName + "'.");
            }
        } catch (SQLException e) {
            logError("Error checking if player has visited warp: ", e);
        }
        return false;
    }

    public static boolean hasRatedPlayerWarp(Player player, String warpName) {
        GamePlayer gamePlayer = Core.getPlatformCompatibility().getGamePlayer(player);

        try {
            String sql = "SELECT rating FROM " + TABLE_NAME + " WHERE name = ? AND user_id = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());
            ps.setLong(2, gamePlayer.getId());

            try {
                @Cleanup ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int rating = rs.getInt("rating");
                    return rating != 0;
                }
            } catch (SQLException e) {
                logError("Error checking if player has rated warp: ", e);
            }
        } catch (SQLException e) {
            logError("Error checking if player has rated warp: ", e);
        }
        return false;
    }

    public static Optional<Location> getPlayerWarpLocation(String warpName) {
        try {
            String sql = "SELECT x, y, z, world FROM " + TABLE_NAME + " WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());

            try {
                @Cleanup ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    String world = rs.getString("world");
                    Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                    return Optional.of(loc);
                }
            } catch (SQLException e) {
                logError("Error getting warp location: ", e);
            }
        } catch (SQLException e) {
            logError("Error getting warp location: ", e);
        }
        return Optional.empty();
    }

    public static void teleportToPlayerWarp(Player player, String warpName) {
        Optional<Location> warpLocation = getPlayerWarpLocation(warpName);

        if (warpLocation.isPresent()) {
            player.teleport(warpLocation.get());
            incrementVisitCount(warpName);
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been teleported to player warp <red>" + warpName);
        } else {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player warp with this name does not exist!");
        }
    }

    public static void incrementVisitCount(String warpName) {
        try {
            String sql = "UPDATE " + TABLE_NAME + " SET visit_count = visit_count + 1 WHERE name = ?";

            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, warpName.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            logError("Error incrementing visit count: ", e);
        }
    }

    public static void logError(String message, Exception e) {
        Main.getInstance().getLogger().log(Level.SEVERE, message, e);
    }

    private static void executeUpdate(String sql) {
        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logError("Error executing update: ", e);
        }
    }
}
