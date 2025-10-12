package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class HorizonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("horizon.admin.reload")) {
            displayAbout(sender);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            ConfigManager.reloadAllConfigs();
            sender.sendMessage(Lang.getPrefix("Horizon") + "<green>All configurations have been reloaded.");
            return true;
        }

        displayHelp(sender);
        return true;
    }

    private void displayAbout(CommandSender sender) {
        sender.sendMessage("<white>");
        sender.sendMessage(Lang.getPrefix("Horizon") + "<gray>Made with <red>❤ <gray>by <red>jogg15");
        sender.sendMessage("<white>");
    }

    private void displayHelp(CommandSender sender) {
        try {
            URL contactUrl = new URI("https://discord.com/users/535531981727989762").toURL();

            sender.sendMessage("<white>");
            sender.sendMessage(Lang.getPrefix("Horizon") + "<gray>Made with <red>❤ <gray>by <click:open_url:"
                    + contactUrl + "><red>jogg15</click>");
            sender.sendMessage("<white>");
        } catch (Exception e) {
            Bukkit.getLogger().severe("[Horizon] Failed to create contact URL: " + e.getMessage());
            if (Bukkit.getPluginManager().getPlugin("Horizon") != null) {
                Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Horizon"))
                        .getLogger().log(Level.SEVERE, "Error in displayHelp()", e);
            }
        }

        if (sender instanceof Player player) {
            TextComponent clickableCommand = new TextComponent("<dark_gray>【 <red>/horizon reload <dark_gray>】 <gray>- Reload all configurations");
            clickableCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("<gray>Click to fill chat")));
            clickableCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/horizon reload"));

            player.spigot().sendMessage(clickableCommand);
            sender.sendMessage("<white>");
        }
    }
}
