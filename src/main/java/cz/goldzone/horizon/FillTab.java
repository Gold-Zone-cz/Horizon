package cz.goldzone.horizon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class FillTab implements TabCompleter {

    private final List<String> nexusArguments = List.of("reload");
    private final List<String> playerWarpsArguments = List.of("create", "delete", "list");
    private final List<String> warpArguments = List.of("reload", "setwarp", "delwarp");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (command.getName().equalsIgnoreCase("horizon") && sender.hasPermission("horizon.admin.reload")) {
                return nexusArguments;
            }
            if (command.getName().equalsIgnoreCase("pwarp")) {
                return playerWarpsArguments;
            }
            if (command.getName().equalsIgnoreCase("warp") && sender.hasPermission("horizon.warp.admin")) {
                return warpArguments;
            }
        }

        return null;
    }
}
