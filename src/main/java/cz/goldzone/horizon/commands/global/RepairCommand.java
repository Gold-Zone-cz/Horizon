package cz.goldzone.horizon.commands.global;

import cz.goldzone.horizon.managers.MoneyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

public class RepairCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir() || !(item.getItemMeta() instanceof Damageable damageable)) {
            player.sendMessage(Lang.getPrefix("Repair") + "<red>You are holding an invalid or non-repairable item!");
            return true;
        }

        if (damageable.getDamage() <= 0) {
            player.sendMessage(Lang.getPrefix("Repair") + "<gray>This item is already fully repaired!");
            return true;
        }

        double repairCost = calculateRepairCost(damageable.getDamage());

        MoneyManager moneyManager = new MoneyManager(player);
        double playerBalance = moneyManager.getAmount();

        if (playerBalance < repairCost) {
            player.sendMessage(Lang.getPrefix("Repair") + "<gray>You don't have enough money. Repair costs <red>$" + repairCost);
            return true;
        }

        moneyManager.subtract((int) repairCost);
        damageable.setDamage(0);
        item.setItemMeta(damageable);

        player.sendMessage(Lang.getPrefix("Repair") + "<gray>You have successfully repaired your item for <green>$" + repairCost);

        return true;
    }

    private double calculateRepairCost(int damage) {
        return damage * 2.5;
    }
}
