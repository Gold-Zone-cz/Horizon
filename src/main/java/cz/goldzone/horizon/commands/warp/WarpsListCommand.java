package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpsListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        player.sendMessage("<white>");
        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Available warps:");
        player.sendMessage("<white>");

        Configuration config = ConfigManager.getConfig("warps");
        ConfigurationSection warpSection = config.getSection("Warps.");

        if (warpSection == null || warpSection.getKeys().isEmpty()) {
            player.sendMessage("<dark_gray>【 <red>No warps available. <dark_gray>】");
        } else {

            for (String warpName : warpSection.getKeys()) {
                TextComponent warpComponent = new TextComponent("§8【 §c" + warpName + " §8】");
                warpComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to fill chat")));
                warpComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp " + warpName));
                player.spigot().sendMessage(warpComponent);
            }
        }

        player.sendMessage("<white>");
        return true;
    }
}