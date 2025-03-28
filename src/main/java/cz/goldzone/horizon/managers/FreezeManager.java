package cz.goldzone.horizon.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager implements Listener {
    private static final Set<UUID> frozenPlayers = new HashSet<>();

    public static boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    public static void freezePlayer(Player player) {
        frozenPlayers.add(player.getUniqueId());
    }

    public static void unfreezePlayer(Player player) {
        frozenPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            unfreezePlayer(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            if (event.getTo() == null) {
                return;
            }

            if (event.getFrom().distance(event.getTo()) > 0) {
                event.setTo(event.getFrom());
            }
        }
    }
}