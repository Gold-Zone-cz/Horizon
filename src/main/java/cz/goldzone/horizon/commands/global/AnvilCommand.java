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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AnvilCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (player.hasPermission("horizon.player.anvil")) {
            Inventory anvilInventory = Bukkit.createInventory(player, InventoryType.ANVIL, "Anvil");
            player.openInventory(anvilInventory);
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

        if (inventory.getType() == InventoryType.ANVIL) {
            ItemStack itemInLeftSlot = inventory.getItem(0);
            ItemStack itemInRightSlot = inventory.getItem(1);
            ItemStack resultItem = inventory.getItem(2);

            if (itemInLeftSlot != null && itemInLeftSlot.getType() != Material.AIR) {
                if (!player.getInventory().addItem(itemInLeftSlot).isEmpty()) {
                    dropItem(player, itemInLeftSlot);
                }
            }
            if (itemInRightSlot != null && itemInRightSlot.getType() != Material.AIR) {
                if (!player.getInventory().addItem(itemInRightSlot).isEmpty()) {
                    dropItem(player, itemInRightSlot);
                }
            }

            if (resultItem != null && resultItem.getType() != Material.AIR) {
                if (!player.getInventory().addItem(resultItem).isEmpty()) {
                    dropItem(player, resultItem);
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
