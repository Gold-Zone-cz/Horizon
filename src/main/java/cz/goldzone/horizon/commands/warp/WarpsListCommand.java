package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WarpsListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        FileConfiguration warpsConfig = Main.getConfigManager().getConfig("warps.yml");

        player.sendMessage("<white>");
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available warps:");
        player.sendMessage("<white>");

        Set<String> warpKeys = warpsConfig.getKeys(false);
        if (warpKeys.isEmpty()) {
            player.sendMessage("<dark_gray>【 <red>No warps available. <dark_gray>】");
        } else {
            for (String key : warpKeys) {
                TextComponent warpText = new TextComponent("<dark_gray>【 <red>" + key + " <dark_gray>】");
                warpText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("<gray>Click to insert into chat").create()));
                warpText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp " + key));
                player.spigot().sendMessage(warpText);
            }
        }

        player.sendMessage("<white>");
        return true;
    }
}