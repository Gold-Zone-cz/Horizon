package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VoteManager {
    private static final HashMap<String, Integer> votes = new HashMap<>();


    public static void loadVotes() {
        votes.clear();

        FileConfiguration config = Main.getConfigManager().getConfig("votes.yml");

        if (config == null) {
            System.out.println("[Horizon] Error: votes.yml could not be loaded!");
            return;
        }

        if (!config.contains("votes")) {
            config.createSection("votes");
            Main.getConfigManager().saveConfig("votes.yml");
        }

        for (String key : Objects.requireNonNull(config.getConfigurationSection("votes")).getKeys(false)) {
            votes.put(key, config.getInt("votes." + key));
        }
    }

    public static int getVotes(String player) {
        return votes.getOrDefault(player, 0);
    }


    public static void addVote(String player) {
        FileConfiguration config = Main.getConfigManager().getConfig("votes.yml");
        int currentVotes = getVotes(player);
        config.set("votes." + player, currentVotes + 1);
        try {
            config.save(Main.getConfigManager().getConfig("votes.yml").saveToString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        votes.put(player, currentVotes + 1);
    }

    public static LinkedHashMap<String, Integer> getTopVoters() {
        HashMap<String, Integer> result = new HashMap<>(votes);
        return result.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}