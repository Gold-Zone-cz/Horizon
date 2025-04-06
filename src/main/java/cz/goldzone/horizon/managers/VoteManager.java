package cz.goldzone.horizon.managers;

import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class VoteManager {
    private static final Map<String, Integer> votes = new HashMap<>();

    public static void loadVotes() {
        votes.clear();
        Configuration config = ConfigManager.getConfig("votes");

        ConfigurationSection votesSection = config.getSection("Votes");
        if (votesSection == null) {
            votesSection = new ConfigurationSection();
            config.set("Votes", votesSection);
            config.save();
        }

        for (String key : votesSection.getKeys()) {
            votes.put(key, config.getInt("Votes." + key));
        }
    }

    public static int getVotes(String player) {
        return votes.getOrDefault(player, 0);
    }

    public static void addVote(String player) {
        int currentVotes = getVotes(player);
        Configuration config = ConfigManager.getConfig("votes");

        config.set("Votes." + player, currentVotes + 1);
        config.save();
        votes.put(player, currentVotes + 1);
    }

    public static LinkedHashMap<String, Integer> getTopVoters() {
        return votes.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}