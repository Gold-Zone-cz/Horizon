package cz.goldzone.horizon.commands.global;

import cz.goldzone.horizon.managers.TimeVoteManager;
import cz.goldzone.horizon.misc.TimeVoteHandler;
import cz.goldzone.horizon.gui.ConfirmGUI;
import cz.goldzone.neuron.shared.Lang;
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

        if (args.length == 1 && args[0].equalsIgnoreCase("end")) {
            if (!player.hasPermission("horizon.staff.timevote.end")) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You do not have permission to end the time vote.");
                return true;
            }

            if (!TimeVoteManager.isVoteInProgress()) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>No vote is currently in progress.");
                return true;
            }

            TimeVoteManager.endVote();
            player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>The time vote has been forcibly ended by staff <red>" + player.getName());
            return true;
        }

        if (args.length == 1 && (args[0].equalsIgnoreCase("day") || args[0].equalsIgnoreCase("night"))) {
            if (!player.hasPermission("horizon.timevote.start")) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You don't have permission to start the vote.");
                return true;
            }

            if (TimeVoteManager.isVoteInProgress()) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>A vote is already in progress.");
                return true;
            }

            if (!TimeVoteManager.canStartNewVote()) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You must wait before starting another vote.");
                return true;
            }

            TimeVoteManager.startVote(args[0].toLowerCase(), player);
            player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>You started a vote for <red>" + args[0].toLowerCase());
            return true;
        }

        if (args.length == 0) {
            if (!TimeVoteManager.isVoteInProgress()) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>No vote is currently in progress.");
                player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>Usage: <red>/timevote <day|night>");
                return true;
            }

            if (player.equals(TimeVoteManager.getVoteCreator())) {
                player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You cannot vote since you created the vote.");
                return true;
            }

            String voteOption = TimeVoteManager.getVoteOption();

            ConfirmGUI confirmGUI = new ConfirmGUI(() -> voteHandler.handleVote(player, voteOption));
            player.openInventory(confirmGUI.getInventory());
            player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>You have opened the voting for <red>" + voteOption + "<gray>.");
            return true;
        }

        player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>Usage: <red>/timevote <day|night>");
        return true;
    }
}
