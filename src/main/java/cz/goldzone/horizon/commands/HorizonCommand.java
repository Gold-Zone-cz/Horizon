package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        sender.sendMessage("<white>");
        sender.sendMessage(Lang.getPrefix("Horizon") + "<gray>Made with <red>❤ <gray>by <red>jogg15");
        sender.sendMessage("<white>");

        if (sender instanceof Player player) {
            Component clickableCommand = Component.text("【 ")
                    .color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("/horizon reload", NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to fill chat", NamedTextColor.GRAY)))
                            .clickEvent(ClickEvent.suggestCommand("/horizon reload")))
                    .append(Component.text(" 】", NamedTextColor.DARK_GRAY))
                    .append(Component.text(" - Reload all configurations", NamedTextColor.GRAY));

            player.sendMessage(String.valueOf(clickableCommand));

            sender.sendMessage("<white>");
        }
    }
}
