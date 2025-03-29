package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.gui.CategoriesGUI;
import cz.goldzone.horizon.gui.ConfirmGUI;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.horizon.gui.CreateGUI;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerWarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length == 0) {
            player.openInventory(new CategoriesGUI().getInventory());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(Lang.getPrefix("PlayerWarps") + "<red>You must provide a warp name!");
                    return false;
                }
                player.openInventory(new CreateGUI(args[1].toLowerCase()).getInventory());
                return true;

            case "delete":
                if (args.length < 2) {
                    player.sendMessage(Lang.getPrefix("PlayerWarps") + "<red>You must provide a name to delete!");
                    return false;
                }

                String warpName = args[1].toLowerCase();
                player.openInventory(new ConfirmGUI(() -> {

                    PlayerWarpsManager.deletePlayerWarp(player, warpName);
                    player.sendMessage(Lang.getPrefix("PlayerWarps") + "<gray>Player warp <red>" + warpName + " <gray>has been deleted!");
                }).getInventory());
                return true;

            case "list":
                player.openInventory(new CategoriesGUI().getInventory());
                return true;

            default:
                return teleportToPlayerWarp(player, args[0]);
        }
    }

    private boolean teleportToPlayerWarp(Player player, String warpName) {
        Location location = PlayerWarpsManager.getPlayerWarpLocation(warpName);
        if (location == null) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "<red>Player warp with this name does not exist!");
            return false;
        }
        player.teleport(location);
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<green>Teleported to player warp " + warpName);
        return true;
    }
}
