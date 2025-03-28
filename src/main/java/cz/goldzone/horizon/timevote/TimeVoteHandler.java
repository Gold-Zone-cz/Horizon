package cz.goldzone.horizon.timevote;

import cz.goldzone.horizon.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class TimeVoteHandler extends BukkitRunnable {

    @Override
    public void run() {
        int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
        int requiredVotes = (int) Math.ceil(onlinePlayersCount * 0.15);

        if (TimeVote.getYesVotes().size() + TimeVote.getNoVotes().size() < requiredVotes) {
            notifyPlayers("<red>Vote failed: Not enough players voted.");
            TimeVote.resetVotes();
            return;
        }

        boolean votePassed = TimeVote.getYesVotes().size() >= TimeVote.getNoVotes().size();
        String resultMessage = votePassed
                ? String.format("<green>Vote passed! Time set to: <gray>%s", TimeVote.currentVote.getName())
                : String.format("<red>Vote failed! (%d YES, %d NO)", TimeVote.getYesVotes().size(), TimeVote.getNoVotes().size());

        if (votePassed) {
            applyTimeChange();
        } else {
            notifyPlayers(resultMessage);
        }

        TimeVote.resetVotes();
    }

    private void notifyPlayers(String message) {
        Bukkit.broadcastMessage(message);
    }

    private void applyTimeChange() {
        String worldName = Main.getConfigManager().getConfig("config.yml").getString("timeVoteWorld");

        World world = Bukkit.getWorld(Objects.requireNonNull(worldName));

        if (world != null) {
            world.setTime(TimeVote.currentVote.getTime());
            notifyPlayers("<green>Time has been successfully changed!");
        }
    }
}
