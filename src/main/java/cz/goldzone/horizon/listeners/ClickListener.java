package cz.goldzone.horizon.listeners;

import cz.goldzone.horizon.gui.PlayerWarpsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof PlayerWarpsGUI playerWarpsGUI) {
            playerWarpsGUI.handleClick(event);
        }
    }
}
