package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.TeleportManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class TpaAcceptCommand implements CommandExecutor {
    private final TeleportManager teleportManager;

    public TpaAcceptCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player requestTarget)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!teleportManager.hasTeleportRequest(requestTarget)) {
            requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<red>You don't have any pending teleport requests.");
            return true;
        }

        TeleportManager.TeleportRequest request = teleportManager.getTeleportRequest(requestTarget);
        Player requestSender = request.getSender();

        if (requestSender == null || !requestSender.isOnline()) {
            requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<red>Player who sent you the request is offline.");
            teleportManager.removeTeleportRequest(requestTarget);
            return true;
        }

        requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<green>Teleportation request accepted.");
        requestSender.sendMessage(Lang.getPrefix("Teleport") + "<gray>Your request was accepted by <red>" + requestTarget.getName() + "<gray>! <red>Do not move for 3 seconds.");

        Location originalLocation = requestSender.getLocation().clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!requestSender.isOnline() || !requestTarget.isOnline()) {
                    handleOfflinePlayers(requestSender, requestTarget);
                    return;
                }

                if (requestSender.getLocation().distanceSquared(originalLocation) > 0) {
                    requestSender.sendMessage(Lang.getPrefix("Teleport") + "<red>Teleportation request cancelled.");
                } else {
                    performTeleport(requestSender, requestTarget);
                }
            }
        }.runTaskLater(Main.getInstance(), 60L);

        return true;
    }

    private void handleOfflinePlayers(Player requestSender, Player requestTarget) {
        Player onlinePlayer = requestSender.isOnline() ? requestSender : requestTarget;

        onlinePlayer.sendMessage(Lang.getPrefix("Teleport") + "<red>Player who sent you the request is offline.");
        teleportManager.removeTeleportRequest(requestSender);
        teleportManager.removeTeleportRequest(requestTarget);
    }

    private void performTeleport(Player requestSender, Player requestTarget) {
        requestSender.teleport(requestTarget);
        requestSender.sendMessage(Lang.getPrefix("Teleport") + "<gray>Teleported to <red>" + requestTarget.getName() + "<gray>.");
        requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<gray>Teleported <red>" + requestSender.getName() + "<gray> to you.");

        teleportManager.removeTeleportRequest(requestSender);
        teleportManager.removeTeleportRequest(requestTarget);
    }
}