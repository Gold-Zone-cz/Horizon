package cz.goldzone.horizon.placeholders;

import cz.goldzone.horizon.managers.VoteManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VotePlaceholders extends PlaceholderExpansion {

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "Horizon";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "jogg15";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;

        if (identifier.startsWith("top_key_")) {
            return getTopVoterName(identifier.replace("top_key_", ""));
        }

        if (identifier.startsWith("top_value_")) {
            return getTopVoterVotes(identifier.replace("top_value_", ""));
        }

        if (identifier.equals("player_votes")) {
            return String.valueOf(VoteManager.getVotes(player.getName()));
        }

        return null;
    }

    private String getTopVoterName(String placeStr) {
        try {
            int place = Integer.parseInt(placeStr);
            return (String) VoteManager.getTopVoters().keySet().toArray()[place - 1];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return "Invalid position";
        }
    }

    private String getTopVoterVotes(String placeStr) {
        try {
            int place = Integer.parseInt(placeStr);
            return String.valueOf(VoteManager.getTopVoters().values().toArray()[place - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return "Invalid position";
        }
    }
}

