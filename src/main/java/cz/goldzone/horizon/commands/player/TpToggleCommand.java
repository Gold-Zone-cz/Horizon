package cz.goldzone.horizon.commands.player;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class TpToggleCommand implements CommandExecutor {
    private static final HashMap<UUID, Boolean> tpToggleMap = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.player.tptoggle")) {
            player.sendMessage(Lang.getPrefix("VIP") + "<red>You need VIP rank to use this command! Use /vip for more information.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        boolean isEnabled = tpToggleMap.getOrDefault(playerUUID, true);
        tpToggleMap.put(playerUUID, !isEnabled);

        String status = isEnabled ? "<red>disabled" : "<green>enabled";
        player.sendMessage(Lang.getPrefix("Economy") + "<gray>Teleports are now " + status);

        return true;
    }

    public static boolean isTpEnabled(Player player) {
        return tpToggleMap.getOrDefault(player.getUniqueId(), true);
    }
}
