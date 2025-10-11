package cz.goldzone.horizon.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final Map<Player, TeleportRequest> activeRequests = new HashMap<>();

    public record TeleportRequest(Player sender, Player target) {
    }

    public void addTpaRequest(Player sender, Player target) {
        TeleportRequest request = new TeleportRequest(sender, target);
        activeRequests.put(sender, request);
        activeRequests.put(target, request);
    }

    public TeleportRequest getTeleportRequest(Player player) {
        return activeRequests.get(player);
    }

    public void removeTeleportRequest(Player player) {
        TeleportRequest request = activeRequests.remove(player);
        if (request != null) {
            activeRequests.remove(request.sender());
            activeRequests.remove(request.target());
        }
    }

    public boolean hasTeleportRequest(Player player) {
        return activeRequests.containsKey(player);
    }
}