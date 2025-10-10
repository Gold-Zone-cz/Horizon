package cz.goldzone.horizon.listeners;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.horizon.managers.JailManager;
import dev.digitality.digitalconfig.config.Configuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final MiniMessage mini = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        JailManager.check(player);

        String title;
        String subtitle;

        if (!player.hasPlayedBefore()) {
            title = toLegacy("<bold><gradient:#2CDB54:#54B65F>SURVIVAL</gradient></bold>");
            subtitle = toLegacy("<gray>Welcome, <green>" + player.getName() +
                    " <gray>! Need help? Use <yellow>/tutorial");

            Configuration config = ConfigManager.getConfig("config");
            Location location = config.get("spawn.location", Location.class);

            player.teleport(location);

        } else {
            title = toLegacy("<bold><gradient:#2CDB54:#54B65F>SURVIVAL</gradient></bold>");
            subtitle = toLegacy("<gray>Welcome back, <green>" + player.getName());

        }
        player.sendTitle(title, subtitle, 10, 70, 20);
    }

    private String toLegacy(String miniMsg) {
        Component parsed = mini.deserialize(miniMsg);
        return legacy.serialize(parsed);
    }
}
