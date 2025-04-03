package cz.goldzone.horizon.admin;

import cz.goldzone.horizon.managers.ConfigManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

public class XrayNotify implements Listener {
    private static final List<Material> XRAY_MATERIALS = Arrays.asList(
            Material.ANCIENT_DEBRIS,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE
    );


    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (!XrayWait.can(player)) {
            return;
        }

        List<String> xrayWorlds = ConfigManager.getConfig("config").getStringList("xrayWorlds");

        if (isXRayMaterial(e.getBlock().getType()) && isInTrackedWorld(e.getBlock().getWorld().getName(), xrayWorlds)) {
            TextComponent text = new TextComponent(player.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spectate " + player.getName()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport!").create()));

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("horizon.staff.xray.notify")) {
                    p.spigot().sendMessage(text);
                }
            }
        }
    }

    private boolean isXRayMaterial(Material material) {
        return XRAY_MATERIALS.contains(material);
    }

    private boolean isInTrackedWorld(String worldName, List<String> xrayWorlds) {
        return xrayWorlds.contains(worldName);
    }
}