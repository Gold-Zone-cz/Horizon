package cz.goldzone.horizon.managers;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final Map<Player, TeleportRequest> activeRequests = new HashMap<>();

    @Getter
    public static class TeleportRequest {
        private final Player sender;
        private final Player target;

        public TeleportRequest(Player sender, Player target) {
            this.sender = sender;
            this.target = target;
        }
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
            activeRequests.remove(request.getSender());
            activeRequests.remove(request.getTarget());
        }
    }

    public boolean hasTeleportRequest(Player player) {
        return activeRequests.containsKey(player);
    }
}