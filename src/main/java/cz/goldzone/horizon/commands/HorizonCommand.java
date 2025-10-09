package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HorizonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("horizon.admin.reload")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                ConfigManager.reloadAllConfigs();
                sender.sendMessage(Lang.getPrefix("Horizon") + "§aAll configurations have been reloaded.");
                return true;
            }
        }

        displayHelp(sender);
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage("<white>");
        sender.sendMessage(Lang.getPrefix("Horizon") + "<gray>Made with <red>❤ <gray>by <white>jogg15");
        sender.sendMessage("<white>");
        sender.sendMessage("<#333333>【 <red>/horizon reload <#333333>】 <gray>Reload all configurations");
        sender.sendMessage("<white>");
    }
}