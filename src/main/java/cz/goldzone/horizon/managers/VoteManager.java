package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import dev.digitality.digitalconfig.config.Configuration;
import dev.digitality.digitalconfig.config.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class VoteManager {
    private static final HashMap<String, Integer> votes = new HashMap<>();


    public static void loadVotes() {
        votes.clear();

        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "votes.yml");

        if (config.getSection("votes") == null) {
            config.set("votes", new ConfigurationSection());
            Main.getConfigManager().saveConfig("votes.yml");
        }

        for (String key : Objects.requireNonNull(config.getSection("votes")).getKeys()) {
            votes.put(key, config.getInt("votes." + key));
        }
    }

    public static int getVotes(String player) {
        return votes.getOrDefault(player, 0);
    }


    public static void addVote(String player) {
        int currentVotes = getVotes(player);

        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "votes.yml");

        config.set("votes." + player, currentVotes + 1);
        config.save();

        votes.put(player, currentVotes + 1);
    }

    public static LinkedHashMap<String, Integer> getTopVoters() {
        HashMap<String, Integer> result = new HashMap<>(votes);
        return result.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}