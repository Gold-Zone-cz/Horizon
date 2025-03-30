package cz.goldzone.horizon.commands.global;

import cz.goldzone.horizon.managers.EconomyManager;
import cz.goldzone.neuron.shared.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RepairCommand implements CommandExecutor {

    private static final double REPAIR_COST_PER_DAMAGE = 2.5;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isRepairableItem(item)) {
            player.sendMessage(Lang.getPrefix("Repair") + "<red>You are holding an invalid or non-repairable item!");
            return true;
        }

        Damageable damageable = (Damageable) item.getItemMeta();
        if (damageable != null && damageable.getDamage() <= 0) {
            player.sendMessage(Lang.getPrefix("Repair") + "<gray>This item is already fully repaired!");
            return true;
        }

        double repairCost = calculateRepairCost(Objects.requireNonNull(damageable).getDamage());

        if (!hasEnoughBalance(player, repairCost)) {
            player.sendMessage(Lang.getPrefix("Repair") + "<gray>You don't have enough money. Repair costs <red>$" + repairCost);
            return true;
        }

        performRepair(player, damageable, item, repairCost);

        return true;
    }

    private boolean isRepairableItem(ItemStack item) {
        return !item.getType().isAir() && item.getItemMeta() instanceof Damageable;
    }

    private double calculateRepairCost(int damage) {
        return damage * REPAIR_COST_PER_DAMAGE;
    }

    private boolean hasEnoughBalance(Player player, double repairCost) {
        return EconomyManager.getBalance(player) >= repairCost;
    }

    private void performRepair(Player player, Damageable damageable, ItemStack item, double repairCost) {
        EconomyManager.withdraw(player, repairCost);
        damageable.setDamage(0);
        item.setItemMeta(damageable);
        player.sendMessage(Lang.getPrefix("Repair") + "<gray>You have successfully repaired your item for <green>$" + repairCost);
    }
}
