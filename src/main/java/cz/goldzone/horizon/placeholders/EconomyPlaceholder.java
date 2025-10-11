package cz.goldzone.horizon.placeholders;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.managers.EconomyManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconomyPlaceholder extends PlaceholderExpansion {
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
        return Main.getInstance().getDescription().getVersion();
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

        if (identifier.equals("horizon_player_money")) {
            double balance = EconomyManager.getBalance(player);
            return formatCurrency(balance);
        }

        return null;
    }

    public static String formatCurrency(double amount) {
        return String.format("$%,.0f", amount).replace(",", " ");
    }
}