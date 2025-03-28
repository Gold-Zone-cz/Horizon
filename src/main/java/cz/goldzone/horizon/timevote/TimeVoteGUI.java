package cz.goldzone.horizon.timevote;

import com.cryptomorin.xseries.XMaterial;
import dev.digitality.digitalgui.api.IGUI;
import dev.digitality.digitalgui.api.InteractiveItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TimeVoteGUI implements IGUI {

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "TimeVote");

        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
        for (int borderSlot : borderSlots) {
            InteractiveItem borderItem = new InteractiveItem(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()), borderSlot);
            borderItem.setDisplayName(" ");
            inv.setItem(borderItem.getSlot(), borderItem);
        }

        InteractiveItem dayVoteItem = new InteractiveItem(Objects.requireNonNull(XMaterial.WHITE_WOOL.parseItem()), 12);
        dayVoteItem.setDisplayName("<yellow>Vote for Day");
        dayVoteItem.onClick((player, clickType) -> {
            player.sendMessage("<gray>You voted for Day!");
            if (TimeVote.addYesVote(player.getName())) {
                TimeVote.setCurrentVote(TimeVoteType.DAY);
            } else {
                player.sendMessage("<red>You can only vote once!");
            }
        });
        inv.setItem(dayVoteItem.getSlot(), dayVoteItem);

        InteractiveItem nightVoteItem = new InteractiveItem(Objects.requireNonNull(XMaterial.BLACK_WOOL.parseItem()), 14);
        nightVoteItem.setDisplayName("<blue>Vote for Night");
        nightVoteItem.onClick((player, clickType) -> {
            player.sendMessage("<gray>You voted for Night!");
            if (TimeVote.addYesVote(player.getName())) {
                TimeVote.setCurrentVote(TimeVoteType.NIGHT);
            } else {
                player.sendMessage("<red>You can only vote once!");
            }
        });
        inv.setItem(nightVoteItem.getSlot(), nightVoteItem);

        return inv;
    }
}
