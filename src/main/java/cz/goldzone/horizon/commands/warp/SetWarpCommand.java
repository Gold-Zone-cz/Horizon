package cz.goldzone.horizon.commands.warp;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetWarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.warp")) {
            player.sendMessage(Lang.getPrefix("Warps") + Lang.format(Lang.get("core.no_perm", player)));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>You must provide a name!");
            return false;
        }

        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/warps.yml");

        String name = args[0].toLowerCase();
        if (config.getSection(name) != null) {
            player.sendMessage(Lang.getPrefix("Warps") + "<red>Warp <gray>" + name + " <red>already exists!");
            return false;
        }

        config.set(name, player.getLocation());
        config.save();

        player.sendMessage(Lang.getPrefix("Warps") + "<gray>Warp <red>" + name + " <gray>has been created!");
        return true;
    }
}

