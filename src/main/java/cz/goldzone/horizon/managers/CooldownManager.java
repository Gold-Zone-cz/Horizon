package cz.goldzone.horizon.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {

    private static final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public static boolean isInCooldown(Player player, String command, long cooldownMillis) {
        cooldowns.putIfAbsent(command, new HashMap<>());
        Map<UUID, Long> map = cooldowns.get(command);

        long now = System.currentTimeMillis();
        if (map.containsKey(player.getUniqueId())) {
            long lastUsed = map.get(player.getUniqueId());
            return (now - lastUsed) < cooldownMillis;
        }
        return false;
    }

    public static long getRemaining(Player player, String command, long cooldownMillis) {
        Map<UUID, Long> map = cooldowns.getOrDefault(command, new HashMap<>());
        long now = System.currentTimeMillis();
        if (!map.containsKey(player.getUniqueId())) return 0;

        long remaining = cooldownMillis - (now - map.get(player.getUniqueId()));
        return Math.max(0, remaining);
    }

    public static void setCooldown(Player player, String command) {
        cooldowns.putIfAbsent(command, new HashMap<>());
        cooldowns.get(command).put(player.getUniqueId(), System.currentTimeMillis());
    }
}
