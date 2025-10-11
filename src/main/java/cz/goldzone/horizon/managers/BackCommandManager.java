package cz.goldzone.horizon.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BackCommandManager {

    private static final Map<UUID, Location> lastLocations = new HashMap<>();

    private BackCommandManager() {}

    public static void setLastLocation(Player player, Location location) {
        if (player == null || location == null) return;
        lastLocations.put(player.getUniqueId(), location.clone());
    }

    public static Location getLastLocation(Player player) {
        return lastLocations.get(player.getUniqueId());
    }

    public static void clear(Player player) {
        lastLocations.remove(player.getUniqueId());
    }
}
