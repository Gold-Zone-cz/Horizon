package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.enums.TimeVoteType;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class TimeVoteManager {

    public static TimeVoteType currentVote;

    @Getter
    private static final Set<String> yesVotes = new HashSet<>();

    @Getter
    private static final Set<String> noVotes = new HashSet<>();

    public static final ItemStack dayVoteItem = createVoteItem(Material.WHITE_WOOL, "<gray>Start voting for <yellow>DAY");
    public static final ItemStack nightVoteItem = createVoteItem(Material.BLACK_WOOL, "<gray>Start voting for <yellow>NIGHT");

    public static void setCurrentVote(TimeVoteType voteType) {
        currentVote = voteType;

        yesVotes.clear();
        noVotes.clear();
    }

    public static boolean addYesVote(String playerName) {
        if (!yesVotes.contains(playerName)) {
            yesVotes.add(playerName);
            return true;
        }
        return false;
    }

    public static void addNoVote(String playerName) {
        noVotes.add(playerName);
    }

    public static boolean hasVoted(String playerName) {
        return yesVotes.contains(playerName) || noVotes.contains(playerName);
    }

    public static void resetVotes() {
        yesVotes.clear();
        noVotes.clear();
        currentVote = null;
    }

    private static ItemStack createVoteItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material, 1);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        return item;
    }

}