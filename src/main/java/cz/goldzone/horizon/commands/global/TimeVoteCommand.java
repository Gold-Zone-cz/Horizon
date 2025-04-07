package cz.goldzone.horizon.commands.global;

import cz.goldzone.horizon.managers.TimeVoteManager;
import cz.goldzone.horizon.gui.ConfirmGUI;
import cz.goldzone.horizon.misc.TimeVoteHandler;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimeVoteCommand implements CommandExecutor {

    private final TimeVoteHandler voteHandler;

    public TimeVoteCommand() {
        this.voteHandler = new TimeVoteHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length != 1 || (!args[0].equalsIgnoreCase("day") && !args[0].equalsIgnoreCase("night"))) {
            player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>Usage: <red>/timevote <day|night>");
            return false;
        }

        String voteOption = args[0].toLowerCase();

        if (TimeVoteManager.isVoteInProgress()) {
            player.sendMessage(Lang.getPrefix("TimeVote") + "<red>There is already a vote in progress. Please wait for it to finish.");
            return true;
        }

        if (!TimeVoteManager.canStartNewVote()) {
            player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You cannot start a new vote yet. Please wait for the cooldown to expire.");
            return true;
        }

        Bukkit.broadcastMessage(Lang.getPrefix("TimeVote") + "<gray>A vote for <red>" + voteOption + " <gray>has been initiated! Please vote.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            ConfirmGUI confirmGUI = new ConfirmGUI(() -> voteHandler.handleVote(p, voteOption));
            confirmGUI.openForPlayer(p);
        }

        TimeVoteManager.startVote(voteOption);

        return true;
    }
}
