package cz.goldzone.horizon.commands.player;

import cz.goldzone.horizon.managers.TeleportManager;
import cz.goldzone.neuron.shared.Lang;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpaCommand implements CommandExecutor {
    private final TeleportManager teleportManager;

    public TpaCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Lang.getPrefix("Teleport") + "<red>You must provide a player name!");
            return false;
        }

        Player requestTarget = Bukkit.getPlayer(args[0]);

        if (requestTarget == null) {
            player.sendMessage(Lang.getPrefix("Teleport") + "<red>Player is unavailable or does not exist!");
            return false;
        }

        if (player.equals(requestTarget)) {
            player.sendMessage(Lang.getPrefix("Teleport") + "<red>You can't teleport to yourself!");
            return false;
        }

        if (!TpToggleCommand.isTpEnabled(requestTarget)) {
            player.sendMessage(Lang.getPrefix("Teleport") + "<red>" + requestTarget.getName() + " has teleport requests disabled!");
            return false;
        }

        requestTarget.sendMessage(Lang.getPrefix("Teleport") + "<gray>Player <red>" + player.getName() + "<gray> wants to teleport to you.");

        TextComponent acceptMessage = new TextComponent("<green>[✔ ACCEPT]");
        acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("<gray>Click here to accept!").create()));
        acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));

        TextComponent denyMessage = new TextComponent("<dark_gray>| <red>[✖ DECLINE]");
        denyMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("<gray>Click here to deny!").create()));
        denyMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        requestTarget.spigot().sendMessage(acceptMessage, denyMessage);

        teleportManager.addTpaRequest(player, requestTarget);
        player.sendMessage(Lang.getPrefix("Teleport") + "<gray>Teleportation request sent to <red>" + requestTarget.getName());

        return true;
    }
}
