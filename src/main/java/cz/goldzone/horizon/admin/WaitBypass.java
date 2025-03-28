package cz.goldzone.horizon.admin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WaitBypass {
    private static final Map<String, Long> delay = new HashMap<>();

    public static boolean can(Player player) {
        String playerName = player.getName();
        long currentTime = System.currentTimeMillis();

        if (delay.containsKey(playerName)) {
            long lastActionTime = delay.get(playerName);

            if (currentTime - lastActionTime > 900_000L) {
                delay.put(playerName, currentTime);
                return true;
            }
            return false;
        }
        delay.put(playerName, currentTime);
        return true;
    }
}
