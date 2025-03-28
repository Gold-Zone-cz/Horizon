package cz.goldzone.horizon.placeholders;

import cz.goldzone.horizon.managers.MoneyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyPlaceholders extends PlaceholderExpansion {
    @NotNull
    @Override
    public String getIdentifier() {
        return "Horizon";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "MrJogger_";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }


    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        if (identifier.equals("player_money")) {
            return String.valueOf(MoneyManager.getMoneyAvailable(player).getAmount());
        }

        return null;
    }
}