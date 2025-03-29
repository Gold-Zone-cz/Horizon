package cz.goldzone.horizon.admin;


import cz.goldzone.neuron.shared.Lang;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetherListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.netherlist")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        List<Player> playersInNether = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getWorld().getName().equals("world_nether")) {
                playersInNether.add(onlinePlayer);
            }
        }

        if (playersInNether.isEmpty()) {
            player.sendMessage(Lang.getPrefix("Admin") + "<gray>No players are in the Nether.");
            return true;
        }

        TextComponent message = new TextComponent(Lang.getPrefix("Admin") + "<gray>Players in the Nether: ");
        player.spigot().sendMessage(message);

        for (Player target : playersInNether) {
            TextComponent playerText = new TextComponent(target.getName());
            playerText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spectate " + target.getName()));
            playerText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Teleport to this player").create()));

            player.spigot().sendMessage(playerText);
        }


        return true;
    }
}
