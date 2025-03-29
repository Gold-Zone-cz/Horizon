package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Category;
import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerWarpsManager {

    public static void createPlayerWarpTable() {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS survival_playerwarps (" +
                            "name VARCHAR(64) PRIMARY KEY," +
                            "owner_uuid VARCHAR(36)," +
                            "category VARCHAR(32)," +
                            "x DOUBLE, y DOUBLE, z DOUBLE," +
                            "world VARCHAR(64))"
            );
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPlayerWarp(Player player, String warpName, Category category) {
        UUID playerId = player.getUniqueId();

        try (Connection connection = Core.getMySQL().getConnection()) {
            String categoryString = category.name();

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO survival_playerwarps (name, owner_uuid, category, x, y, z, world) VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, warpName.toLowerCase());
            ps.setString(2, playerId.toString());
            ps.setString(3, categoryString);
            ps.setDouble(4, player.getLocation().getX());
            ps.setDouble(5, player.getLocation().getY());
            ps.setDouble(6, player.getLocation().getZ());
            ps.setString(7, player.getWorld().getName());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deletePlayerWarp(Player player, String name) {
        UUID ownerUUID = player.getUniqueId();
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM survival_playerwarps WHERE name = ? AND owner_uuid = ?"
            );
            ps.setString(1, name.toLowerCase());
            ps.setString(2, ownerUUID.toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getPlayerWarpLocation(String warpName) {
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT x, y, z, world FROM survival_playerwarps WHERE name = ?"
            );
            ps.setString(1, warpName.toLowerCase());
            @Cleanup ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                return world != null ? new Location(world, x, y, z) : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getPlayerWarps(Player player) {
        List<String> warps = new ArrayList<>();
        UUID ownerUUID = player.getUniqueId();
        try {
            @Cleanup Connection connection = Core.getMySQL().getConnection();
            @Cleanup PreparedStatement ps = connection.prepareStatement(
                    "SELECT name FROM survival_playerwarps WHERE owner_uuid = ?"
            );
            ps.setString(1, ownerUUID.toString());
            @Cleanup ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                warps.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warps;
    }

    public static Category getPlayerWarpCategory(String warpName) {
        try (Connection connection = Core.getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT category FROM survival_playerwarps WHERE name = ?")) {
            ps.setString(1, warpName.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Category.valueOf(rs.getString("category").toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void teleportToPlayerWarp(Player player, String warpName) {
        try (Connection connection = Core.getMySQL().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT x, y, z, world FROM survival_playerwarps WHERE name = ?")) {

            ps.setString(1, warpName.toLowerCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    for (World w : Bukkit.getWorlds()) {
                        if (w.getName().equalsIgnoreCase(worldName)) {
                            world = w;
                            break;
                        }
                    }
                }

                if (world == null) {
                    Main.getInstance().getLogger().warning("World not found: " + worldName);
                    return;
                }

                Location location = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
                player.teleport(location);
                player.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been teleported to player warp <red>" + warpName);
            } else {
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Player warp with this name does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}