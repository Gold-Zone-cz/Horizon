package cz.goldzone.horizon.managers;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FillTabItemManager implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (sender.hasPermission("horizon.admin.give")) {
            if (args.length == 1 && command.getName().equalsIgnoreCase("i")) {
                for (Material material : Material.values()) {
                    if (material.isItem()) {
                        if (args[0].isEmpty() || material.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                            suggestions.add(material.name().toLowerCase());
                        }
                    }
                }
            }
        }

        return suggestions.isEmpty() ? null : suggestions;
    }
}
