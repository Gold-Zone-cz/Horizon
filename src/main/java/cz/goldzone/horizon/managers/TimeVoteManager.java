package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.gui.ConfirmGUI;
import cz.goldzone.horizon.misc.TimeVoteHandler;
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

    public static void startVote(String voteOption) {
        if (isVoteInProgress()) {
            Bukkit.getServer().broadcastMessage(Lang.getPrefix("TimeVote") + "<red>A vote is already in progress. Please wait for it to finish.");
            return;
        }

        voteInProgress = true;
        lastVoteTime = System.currentTimeMillis();

        Bukkit.getServer().broadcastMessage(Lang.getPrefix("TimeVote") + "<gray>A vote for <red>" + voteOption + " <gray>has been initiated! <red>/timevote");

        for (Player player : Bukkit.getOnlinePlayers()) {
            ConfirmGUI confirmGUI = new ConfirmGUI(() -> new TimeVoteHandler().handleVote(player, voteOption));
            player.openInventory(confirmGUI.getInventory());
        }

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
