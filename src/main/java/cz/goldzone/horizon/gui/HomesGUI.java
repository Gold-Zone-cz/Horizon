package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomesGUI implements IGUI {
    private final List<String> homes;
    private final Player player;

    public HomesGUI(List<String> homes, Player player) {
        this.homes = homes;
        this.player = player;
    }


    @NotNull
    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, "Your homes");
        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        int slot = 10;
        for (String homeName : homes) {
            if (slot >= inv.getSize()) break;

            String creationDate = HomesManager.getHomeCreationDate(homeName, player);
            Location homeLocation = HomesManager.getHomeLocation(player, homeName);

            if (homeLocation == null) {
                continue;
            }

            InteractiveItem item = new InteractiveItem(Objects.requireNonNull(XMaterial.RED_BED.parseItem()), slot);
            item.setDisplayName("<red><smallcaps>" + homeName + "</smallcaps> <gray>(" + creationDate + "<gray>)");
            item.setLore("<gray>\n" +
                    "<red>➥ <gray>Location: <red>" + Objects.requireNonNull(homeLocation.getWorld()).getName() + "\n" +
                    "<red>➥ <gray>XYZ: <red>" + homeLocation.getBlockX() + " " + homeLocation.getBlockY() + " " + homeLocation.getBlockZ() + "\n" +
                    "<gray>\n" +
                    "<red>➥ <gray>Left click to <green>teleport\n" + "<red>➥ <gray>Right click to <red>delete\n" + "<gray>");
            item.onLeftClick((player) -> {
                player.teleport(homeLocation);
                player.sendMessage(Lang.getPrefix("Homes") + "<gray>You have been teleported to home <red>" + homeName);
            });

            item.onRightClick(player ->
                    player.openInventory(new ConfirmGUI(() -> {
                        HomesManager.deleteHome(player, homeName);
                        player.sendMessage(Lang.getPrefix("Homes") + "<gray>Your home <red>" + homeName + " <gray>has been successfully deleted");
                    }).getInventory())
            );

            inv.setItem(item.getSlot(), item);

            if ((slot - 7) % 9 == 0)
                slot += 3;
            else
                slot++;
        }
        return inv;
    }
}