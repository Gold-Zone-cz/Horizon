package cz.goldzone.horizon.misc;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TimeVoteHandler {


    private final Map<Player, String> votes = new HashMap<>();

    public void handleVote(Player player, String voteOption) {

        votes.put(player, voteOption);
        player.sendMessage(Lang.getPrefix("TimeVote") + "<gray>You have voted for <red>" + voteOption + "<gray>!");

        if (votes.size() == Bukkit.getOnlinePlayers().size()) {
            finishVoting();
        }
    }

    private void finishVoting() {
        int dayVotes = 0;
        int nightVotes = 0;

        for (String vote : votes.values()) {
            if (vote.equalsIgnoreCase("day")) {
                dayVotes++;
            } else if (vote.equalsIgnoreCase("night")) {
                nightVotes++;
            }
        }

        String result = (dayVotes > nightVotes) ? "Day" : "Night";
        Bukkit.broadcastMessage(Lang.getPrefix("TimeVote") + "<gray>The vote has been completed. The result is: <red>" + result + "<gray>!");
        if (result.equals("Day")) {
            Bukkit.getWorlds().forEach(world -> world.setTime(1000));
        } else {
            Bukkit.getWorlds().forEach(world -> world.setTime(13000));
        }

        resetVotes();
    }

    private void resetVotes() {
        votes.clear();
    }
}
