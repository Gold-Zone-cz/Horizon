package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeVoteManager {

    @Getter
    private static boolean voteInProgress = false;
    private static long lastVoteTime = 0;
    private static final long VOTE_COOLDOWN = 60 * 60 * 1000;
    private static final long VOTE_DURATION = 20 * 60 * 5;

    @Getter
    private static String voteOption;
    @Getter
    private static Player voteCreator;

    public static void startVote(String voteOption, Player creator) {
        if (isVoteInProgress()) {
            Bukkit.getServer().broadcastMessage(Lang.getPrefix("TimeVote") + "<red>A vote is already in progress.");
            return;
        }

        voteInProgress = true;
        lastVoteTime = System.currentTimeMillis();
        TimeVoteManager.voteOption = voteOption;
        TimeVoteManager.voteCreator = creator;

        Bukkit.getServer().broadcastMessage(Lang.getPrefix("TimeVote") + "<gray>A vote for <red>" + voteOption + " <gray>has been initiated! <red>/timevote");

        new BukkitRunnable() {
            @Override
            public void run() {
                endVote();
            }
        }.runTaskLater(Main.getInstance(), VOTE_DURATION);
    }

    public static void endVote() {
        if (!voteInProgress) {
            return;
        }
        voteInProgress = false;
    }

    public static boolean canStartNewVote() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastVoteTime >= VOTE_COOLDOWN;
    }

    public static void handleEndVoteCommand(Player player) {
        if (player.hasPermission("horizon.staff.timevote.end")) {
            endVote();
            player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>The time vote has been ended by staff <red>" + player.getName() + "<gray>.");
        } else {
            player.sendMessage(Lang.getPrefix("TimeVote") + "<red>You do not have permission to end the time vote.");
        }
    }
}
