package cz.goldzone.horizon.commands;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.horizon.gui.CategoriesGUI;
import cz.goldzone.horizon.gui.CreateGUI;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerWarpsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command is for players only!");
            return false;
        }

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                return createWarp(player, args);
            }
            case "delete" -> {
                return deleteWarp(player, args);
            }
            case "list" -> {
                player.openInventory(new CategoriesGUI().getInventory());
                return true;
            }
            default -> {
                return teleportToWarp(player, args);
            }
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§f");
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "§7Available commands:");
        player.sendMessage("§f");
        player.sendMessage("§7§l➦ §f/pwarp <name>§8 - §7Teleport to a player warp");
        player.sendMessage("§7§l➦ §f/pwarp list§8 - §7Open the GUI for player warps");
        player.sendMessage("§7§l➦ §f/pwarp create <name>§8 - §7Create a new player warp");
        player.sendMessage("§7§l➦ §f/pwarp delete <name>§8 - §7Delete your player warp");
        player.sendMessage("§f");
    }

    private boolean createWarp(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cYou must provide a name!");
            return false;
        }
        if (Main.getConfigManager().getConfig("player_warps.yml").get(args[1].toLowerCase()) != null) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cA player warp with this name already exists!");
            return false;
        }
        player.openInventory(new CreateGUI(args[1].toLowerCase()).getInventory());
        return true;
    }

    private boolean deleteWarp(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cYou must provide a name!");
            return false;
        }
        String name = args[1].toLowerCase();
        String ownerName = Main.getConfigManager().getConfig("player_warps.yml").getString(name + ".owner");
        if (!Objects.requireNonNull(ownerName).equals(player.getName()) && !player.hasPermission("horizon.pwarps.delete.others")) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cYou cannot delete other players' warps!");
            return false;
        }
        Main.getConfigManager().getConfig("player_warps.yml").set(name, null);
        Main.getConfigManager().saveConfig("player_warps.yml");
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "§aPlayer warp §b" + name + " §ahas been successfully deleted!");
        return true;
    }

    private boolean teleportToWarp(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cYou must provide a warp name!");
            return false;
        }
        String name = args[0].toLowerCase();
        if (Main.getConfigManager().getConfig("player_warps.yml").get(name) == null) {
            player.sendMessage(Lang.getPrefix("PlayerWarps") + "§cNo player warp found with this name!");
            return false;
        }
        Location location = (Location) Objects.requireNonNull(Main.getConfigManager().getConfig("player_warps.yml").get(name + ".playerlocation"));
        player.teleport(location);
        player.sendMessage(Lang.getPrefix("PlayerWarps") + "§aYou have been teleported to player warp §b" + name + "§a!");
        return true;
    }
}
