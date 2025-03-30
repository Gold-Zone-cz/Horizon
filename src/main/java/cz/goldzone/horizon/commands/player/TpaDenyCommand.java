package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.managers.TeleportManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpaDenyCommand implements CommandExecutor {
    private final TeleportManager teleportManager;

    public TpaDenyCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player requestTarget)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        TeleportManager.TeleportRequest request = teleportManager.getTeleportRequest(requestTarget);
        if (request != null) {
            Player requestSender = request.getSender();

            requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<gray>Teleportation request <red>denied");
            requestSender.sendMessage(Lang.getPrefix("Teleport") + "<gray>Your teleport request was denied by <red>" + requestTarget.getName());

            teleportManager.removeTeleportRequest(requestSender);
            teleportManager.removeTeleportRequest(requestTarget);
        } else {
            requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<red>You don't have any pending teleport requests.");
        }
        return true;
    }
}