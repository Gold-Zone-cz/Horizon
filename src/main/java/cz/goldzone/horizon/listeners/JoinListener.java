package cz.goldzone.horizon.listeners;

import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import cz.goldzone.horizon.managers.JailManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        JailManager.check(player);
    }
}
