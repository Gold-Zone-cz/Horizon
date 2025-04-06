package cz.goldzone.horizon.commands.playerwarps;

import cz.goldzone.horizon.gui.CategoriesGUI;
import cz.goldzone.horizon.gui.ConfirmGUI;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.horizon.gui.CreateGUI;
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
            displayHelp(player);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "list" -> handleList(player);
            case "rate" -> handleRate(player, args);
            default -> handleTeleportToWarp(player, args[0]);
        };
    }


    private void displayHelp(Player player) {
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<gray>Available Commands:");
        player.sendMessage("<white>");
        player.sendMessage("<dark_gray>【 <red>/pw create <name> <dark_gray>】 <gray>Create a player warp");
        player.sendMessage("<dark_gray>【 <red>/pw delete <name> <dark_gray>】 <gray>Delete a player warp");
        player.sendMessage("<dark_gray>【 <red>/pw list <dark_gray>】 <gray>List all player warps");
        player.sendMessage("<white>");
        player.sendMessage("<dark_gray>【 <red>/pw rate <name> <rating> <dark_gray>】 <gray>Rate a player warp");
        player.sendMessage("<dark_gray>【 <red>/pw <name> <dark_gray>】 <gray>Teleport to a player warp");
        player.sendMessage("<white>");
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            sendErrorMessage(player, "You must provide a player warp name!");
            return false;
        }

        String warpName = args[1].toLowerCase();

        if (PlayerWarpsManager.getPlayerWarpLocation(warpName).isPresent()) {
            sendErrorMessage(player, "This name already exists!");
            return false;
        }

        player.openInventory(new CreateGUI(warpName).getInventory());
        return true;
    }


    private boolean handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            sendErrorMessage(player, "You must provide a name to delete!");
            return false;
        }

        String warpName = args[1].toLowerCase();
        PlayerWarpsManager.getPlayerWarpOwner(warpName);
        player.getUniqueId();
        boolean isOwner = false;
        boolean canDeleteOther = player.hasPermission("horizon.admin.pwarps.delete");

        if (!isOwner && !canDeleteOther) {
            sendErrorMessage(player, "You do not have permission to delete another player's warp!");
            return false;
        }

        if (PlayerWarpsManager.getPlayerWarpLocation(warpName).isEmpty()) {
            sendErrorMessage(player, "Player warp with this name does not exist!");
            return false;
        }

        player.openInventory(new ConfirmGUI(() -> {
            PlayerWarpsManager.deletePlayerWarp(player, warpName);
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "<gray>Player warp <red>" + warpName + " <gray>has been deleted!");
        }).getInventory());

        return true;
    }

    private boolean handleList(Player player) {
        player.openInventory(new CategoriesGUI().getInventory());
        return true;
    }

    private boolean handleTeleportToWarp(Player player, String warpName) {
        PlayerWarpsManager.getPlayerWarpLocation(warpName).ifPresentOrElse(location -> {
            player.teleport(location);
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "<green>Teleported to player warp " + warpName);
        }, () -> sendErrorMessage(player, "Player warp with this name does not exist!"));
        return true;
    }

    private boolean handleRate(Player player, String[] args) {
        if (args.length < 3) {
            sendErrorMessage(player, "Usage: /pw rate <warp_name> <rating>");
            return false;
        }

        String warpName = args[1].toLowerCase();
        int rating;

        try {
            rating = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sendErrorMessage(player, "Rating must be a number between 1 and 5!");
            return false;
        }

        if (rating < 1 || rating > 5) {
            sendErrorMessage(player, "Rating must be between 1 and 5!");
            return false;
        }

        if (PlayerWarpsManager.getPlayerWarpLocation(warpName).isEmpty()) {
            sendErrorMessage(player, "Player warp with this name does not exist!");
            return false;
        }

        if (PlayerWarpsManager.hasRatedPlayerWarp(player, warpName)) {
            sendErrorMessage(player, "You have already rated this player warp!");
            return false;
        }

        if (!PlayerWarpsManager.hasVisitedPlayerWarp(player, warpName)) {
            sendErrorMessage(player, "You must visit the player warp before rating it!");
            return false;
        }

        PlayerWarpsManager.setPlayerWarpRating(warpName, rating);
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<gray>Successfully rated <green>"
                + warpName + " <gray>with <yellow>" + rating + " stars");
        return true;
    }

    private void sendErrorMessage(Player player, String message) {
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "<red>" + message);
    }
}