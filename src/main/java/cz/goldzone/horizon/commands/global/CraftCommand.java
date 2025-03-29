package cz.goldzone.horizon.commands.global;

import cz.goldzone.neuron.shared.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (player.hasPermission("horizon.player.craft")) {
            player.openInventory(Bukkit.createInventory(player, org.bukkit.event.inventory.InventoryType.WORKBENCH));
        } else {
            player.sendMessage(Lang.getPrefix("VIP") + "<red>You need VIP rank to use this command!\n Use /vip for more information.");
        }

        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (inventory.getType() == org.bukkit.event.inventory.InventoryType.WORKBENCH) {
            for (int i = 0; i < 9; i++) {
                ItemStack itemInSlot = inventory.getItem(i);

                if (itemInSlot != null && itemInSlot.getType() != Material.AIR) {
                    if (player.getInventory().firstEmpty() == -1) {
                        dropItem(player, itemInSlot);
                    } else {
                        player.getInventory().addItem(itemInSlot);
                    }
                    inventory.setItem(i, null);
                }
            }
        }
    }

    private void dropItem(Player player, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            Item droppedItem = player.getWorld().dropItemNaturally(player.getLocation(), item);
            droppedItem.setPickupDelay(40);
        }
    }
}