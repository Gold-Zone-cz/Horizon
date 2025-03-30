package cz.goldzone.horizon.commands.economy;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PayToggleCommand implements CommandExecutor {
    private static final Map<UUID, Boolean> payToggleMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.player.paytoggle")) {
            player.sendMessage(Lang.getPrefix("VIP") + "<red>You need VIP rank to use this command!\n Use /vip for more information.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        boolean isEnabled = payToggleMap.getOrDefault(playerUUID, true);
        payToggleMap.put(playerUUID, !isEnabled);

        String status = isEnabled ? "<red>disabled" : "<green>enabled";
        player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Payments are now " + status);

        return true;
    }

    public static boolean isPayEnabled(@NotNull Player player) {
        return payToggleMap.getOrDefault(player.getUniqueId(), true);
    }
}