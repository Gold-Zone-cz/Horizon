package cz.goldzone.horizon.listeners;

import cz.goldzone.horizon.gui.AllPlayerWarpsGUI;
import cz.goldzone.horizon.gui.PlayerWarpsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Object holder = event.getInventory().getHolder();

        if (holder instanceof PlayerWarpsGUI playerWarpsGUI) {
            handlePlayerWarpsClick(playerWarpsGUI, event);
        } else if (holder instanceof AllPlayerWarpsGUI allPlayerWarpsGUI) {
            handleAllPlayerWarpsClick(allPlayerWarpsGUI, event);
        }
    }

    private void handlePlayerWarpsClick(PlayerWarpsGUI playerWarpsGUI, InventoryClickEvent event) {
        playerWarpsGUI.handleClick(event);
    }

    private void handleAllPlayerWarpsClick(AllPlayerWarpsGUI allPlayerWarpsGUI, InventoryClickEvent event) {
        allPlayerWarpsGUI.handleClick(event);
    }
}
