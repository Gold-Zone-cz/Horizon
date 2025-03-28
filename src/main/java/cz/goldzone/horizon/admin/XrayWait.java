package cz.goldzone.horizon.admin;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class XrayWait {
    private static final Map<String, Long> delay = new HashMap<>();

    public static boolean can(Player player) {
        String playerName = player.getName();
        long currentTime = System.currentTimeMillis();

        if (delay.containsKey(playerName)) {
            long lastUsedTime = delay.get(playerName);

            if (currentTime - lastUsedTime > 900000) {
                delay.put(playerName, currentTime);
                return true;
            } else {
                return false;
            }
        } else {
            delay.put(playerName, currentTime);
            return true;
        }
    }
}
