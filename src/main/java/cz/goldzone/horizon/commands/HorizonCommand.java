package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HorizonCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("horizon.admin.reload")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                ConfigManager configManager = Main.getConfigManager();
                configManager.reloadAllConfigs();
                sender.sendMessage(Lang.getPrefix("Horizon") + "§aAll configurations have been reloaded.");
            }
            if (args.length == 0) {
                sender.sendMessage("<white>");
                sender.sendMessage(Lang.getPrefix("Horizon") + "<gray>Available commands:");
                sender.sendMessage("<white>");
                sender.sendMessage("<#333333>【 <red>/horizon reload <#333333>】 <gray>Reload all configurations");
                sender.sendMessage("<white>");
            }
        } else {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        }


        return false;
    }
}