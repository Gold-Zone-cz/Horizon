package cz.goldzone.horizon.commands.global;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.ConfigManager;
import cz.goldzone.horizon.managers.TimeVoteManager;
import cz.goldzone.horizon.gui.TimeVoteGUI;
import cz.goldzone.horizon.enums.TimeVoteType;
import cz.goldzone.horizon.misc.TimeVoteWait;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TimeVoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (args.length == 0) {
            player.openInventory(new TimeVoteGUI().getInventory());
            return true;
        }

        if ("yes".equalsIgnoreCase(args[0]) || "no".equalsIgnoreCase(args[0])) {
            if (TimeVoteManager.currentVote == null) {
                player.sendMessage("No vote is currently running.");
                return false;
            }

            if (TimeVoteManager.hasVoted(player.getName())) {
                player.sendMessage("You can only vote once.");
                return false;
            }

            if ("yes".equalsIgnoreCase(args[0])) {
                TimeVoteManager.addYesVote(player.getName());
                player.sendMessage("You voted YES.");
            } else {
                TimeVoteManager.addNoVote(player.getName());
                player.sendMessage("You voted NO.");
            }

            return true;
        }

        if (TimeVoteManager.currentVote != null) {
            player.sendMessage("A vote is already in progress.");
            return false;
        }

        if (!TimeVoteWait.can(player)) {
            player.sendMessage("<red>Please wait before starting another vote.");
            return false;
        }

        try {
            TimeVoteType voteType = TimeVoteType.valueOf(args[0].toUpperCase());
            TimeVoteManager.currentVote = voteType;
            TimeVoteManager.addYesVote(player.getName());

            String message = player.getName() + " started a vote to set the time to " + voteType.getName() +
                    ". Vote now using /tv yes or /tv no.";
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                int requiredVotes = (int) Math.ceil(Bukkit.getOnlinePlayers().size() * 0.15);
                if (TimeVoteManager.getYesVotes().size() + TimeVoteManager.getNoVotes().size() < requiredVotes) {
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("Vote failed: Not enough players voted."));
                    TimeVoteManager.resetVotes();
                    TimeVoteManager.resetVotes();
                    return;
                }

                if (TimeVoteManager.getYesVotes().size() >= TimeVoteManager.getNoVotes().size()) {
                    String successMessage = "Vote passed! The time is now set to " + voteType.getName();
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(successMessage));
                    String worldName = ConfigManager.getConfig("config").getString("timeVoteWorld");
                    if (worldName != null) {
                        Objects.requireNonNull(Bukkit.getWorld(worldName)).setTime(voteType.getTime());
                    }
                } else {
                    String failMessage = "Vote failed!";
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(failMessage));
                }

                TimeVoteManager.resetVotes();
            }, 600L);

        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid vote type. Use 'day' or 'night'.");
        }

        return true;
    }
}
