package cz.goldzone.horizon.admin;

import cz.goldzone.neuron.shared.Core;
import lombok.Cleanup;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WaitBypassListener implements Listener {

    @EventHandler
    public void onSign(SignChangeEvent e) {
        if (e.isCancelled()) return;
        if (emptySign(e.getLines())) return;

        if (checkMute(e.getPlayer()) && WaitBypass.can(e.getPlayer())) {
            sendSpectateMessage(e.getPlayer());
        }
    }

    @EventHandler
    public void onBook(PlayerEditBookEvent e) {
        if (e.isCancelled()) return;
        if (emptyBook(e.getNewBookMeta())) return;

        if (checkMute(e.getPlayer()) && WaitBypass.can(e.getPlayer())) {
            sendSpectateMessage(e.getPlayer());
        }
    }

    private boolean checkMute(Player player) {
        String query = "SELECT * FROM litebans_mutes WHERE uuid = ? AND active = 1";
        try {
            @Cleanup Connection connection = Core.getPluginMySQL().getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean emptySign(String[] lines) {
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) return false;
        }
        return true;
    }

    private boolean emptyBook(BookMeta meta) {
        for (String page : meta.getPages()) {
            if (page != null && !page.trim().isEmpty()) return false;
        }
        return true;
    }

    private void sendSpectateMessage(Player player) {
        String playerName = player.getName();
        TextComponent text = new TextComponent(playerName);
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spectate " + playerName));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport!").create()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("horizon.staff.spectate")) {
                p.spigot().sendMessage(text);
            }
        }
    }
}
