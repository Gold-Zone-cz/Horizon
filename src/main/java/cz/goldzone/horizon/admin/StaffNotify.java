package cz.goldzone.horizon.admin;

import cz.goldzone.neuron.shared.Core;
import cz.goldzone.neuron.shared.Lang;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class StaffNotify {

    private static final Logger logger = Logger.getLogger(StaffNotify.class.getName());

    public static void sendMessage(final String msg) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("horizon.staff.notify")) {
                player.sendMessage(Lang.getPrefix("Notify") + msg);
            }
        }
    }

    public static boolean isHidden(final Player player) {
        return Core.getPlatformCompatibility().isVanished(player);
    }

    public static Integer size(final boolean includeHide) {
        int staff = 0;

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("horizon.staff.notify") && (includeHide || !isHidden(player))) {
                ++staff;
            }
        }
        return staff;
    }

    public static void setStaffNotify(Player player, String eventText) {
        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());

        try {
        @Cleanup Connection connection = Core.getMySQL().getConnection();
        @Cleanup PreparedStatement statement = connection.prepareStatement("INSERT INTO events_suspect (NICK, TEXT, TIME) VALUES (?, ?, ?)");
        statement.setString(1, player.getName());
        statement.setString(2, eventText);
        statement.setTimestamp(3, currentTime);
        statement.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Error while inserting event data into the database: " + e.getMessage());
            logger.warning("Stack trace: ");
            for (StackTraceElement element : e.getStackTrace()) {
                logger.warning(element.toString());
            }
        }
    }
}


