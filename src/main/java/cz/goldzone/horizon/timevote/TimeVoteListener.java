package cz.goldzone.horizon.timevote;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TimeVoteListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.getView().getTitle();
        if (!e.getView().getTitle().equals("TimeVote")) {
            return;
        }

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem().equals(TimeVote.dayVoteItem)) {
            player.performCommand("tv day");
            player.closeInventory();
        } else if (e.getCurrentItem().equals(TimeVote.nightVoteItem)) {
            player.performCommand("tv night");
            player.closeInventory();
        }

        e.setCancelled(true);
    }
}
