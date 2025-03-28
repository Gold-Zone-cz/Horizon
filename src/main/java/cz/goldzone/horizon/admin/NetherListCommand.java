package cz.goldzone.horizon.admin;


import cz.goldzone.neuron.shared.Lang;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class NetherListCommand implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().toLowerCase().split(" ");

        if (args[0].equals("/netherlist") && e.getPlayer().hasPermission("horizon.admin.netherlist")) {
            e.setCancelled(true);

            List<Player> playersInNether = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getName().equals("world_nether")) {
                    playersInNether.add(player);
                }
            }

            if (playersInNether.isEmpty()) {
                e.getPlayer().sendMessage(Lang.getPrefix("Admin") + "<gray>No players are in the Nether.");
                return;
            }

            TextComponent message = new TextComponent(Lang.getPrefix("Admin") + "<gray>Players in the Nether: ");
            e.getPlayer().spigot().sendMessage((BaseComponent) message);

            for (Player player : playersInNether) {
                TextComponent playerText = new TextComponent(player.getName());
                playerText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spectate " + player.getName()));
                playerText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Teleport to this player").create()));

                e.getPlayer().spigot().sendMessage((BaseComponent) playerText);
            }
        }
    }
}
