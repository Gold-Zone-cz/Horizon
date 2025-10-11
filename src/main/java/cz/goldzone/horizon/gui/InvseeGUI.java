package cz.goldzone.horizon.gui;

import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record InvseeGUI(Player target) implements IGUI {

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, "Inventory of " + target.getName());

        DigitalGUI.fillInventory(inv, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), null);

        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        if (head != null) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName("<red>" + target.getName());

                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add("<gray>UUID: " + target.getUniqueId());
                lore.add("<gray>Health: <red>" + String.format("%.1f", target.getHealth()) + " ❤");
                lore.add("<gray>Food Level: <red>" + target.getFoodLevel() + " 🍗");
                lore.add("<gray>XP Level: <green>" + target.getLevel());
                lore.add(" ");

                if (!target.getActivePotionEffects().isEmpty()) {
                    lore.add("<gray>Active Effects:");
                    for (PotionEffect effect : target.getActivePotionEffects()) {
                        String key = effect.getType().getTranslationKey()
                                .replace("effect.minecraft.", "");
                        key = Character.toUpperCase(key.charAt(0)) + key.substring(1);

                        lore.add("<dark_gray>- <yellow>" + key +
                                "<gray> (" + (effect.getDuration() / 20) + "s)");
                    }
                } else {
                    lore.add("<gray>Active Effects: <dark_gray>none");
                }
                lore.add(" ");
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
        }
        inv.setItem(4, head);

        ItemStack[] armor = target.getInventory().getArmorContents();
        inv.setItem(45, armor.length > 3 ? armor[3] : new ItemStack(Material.AIR));
        inv.setItem(46, armor.length > 2 ? armor[2] : new ItemStack(Material.AIR));
        inv.setItem(47, armor.length > 1 ? armor[1] : new ItemStack(Material.AIR));
        inv.setItem(48, armor.length > 0 ? armor[0] : new ItemStack(Material.AIR));

        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 0; i < Math.min(contents.length, 36); i++) {
            ItemStack item = contents[i];
            if (item == null) continue;
            InteractiveItem interactiveItem = new InteractiveItem(item);
            interactiveItem.onClick((viewer, clickType) ->
                    viewer.sendMessage(Lang.getPrefix("Horizon") + "<gray>You cannot modify this inventory.")
            );
            inv.setItem(9 + i, interactiveItem);
        }

        inv.setItem(53, target.getInventory().getItemInOffHand());

        return inv;
    }

    public void open(Player viewer) {
        viewer.openInventory(getInventory());
    }
}
