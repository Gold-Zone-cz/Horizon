package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.admin.StaffNotify;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FreezeManager implements Listener {
    private static final Map<Player, Integer> frozenPlayers = new HashMap<>();
    private static final Map<Player, String> freezingStaff = new HashMap<>();

    public static boolean isFrozen(Player player) {
        return frozenPlayers.containsKey(player);
    }

    public static void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isFrozen(player)) {
                        int remainingTime = frozenPlayers.get(player);
                        if (remainingTime > 1) {
                            frozenPlayers.put(player, remainingTime - 1);
                        } else {
                            unfreezePlayer(player);
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 1200L, 1200L);
    }

    public static void unfreezePlayer(Player player) {
        if (isFrozen(player)) {
            frozenPlayers.remove(player);
            freezingStaff.remove(player);
        }
    }

    public static void freezePlayer(Player player, int minutes, String staff, boolean log) {
        if (isFrozen(player)) {
            return;
        }

        frozenPlayers.put(player, minutes);
        freezingStaff.put(player, staff);

        player.setHealth(20);
        player.setFoodLevel(20);

        player.sendTitle("<red><bold>FROZEN!", "<gray>You have been frozen for " + minutes + " minutes.", 10, 70, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);
        if (log) {
            StaffNotify.sendMessage("<red>" + player.getName() +
                    " <gray>has been frozen by <red>" + staff +
                    " <gray>for <red>" + minutes + " <gray>minutes.", false, true);
        }
    }

    private void handleFrozenPlayerActions(Player player, PlayerMoveEvent event) {
        if (isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private void handleFrozenPlayerActions(Player player, PlayerInteractEvent event) {
        if (isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private void handleFrozenPlayerActions(Player player, BlockBreakEvent event) {
        if (isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private void handleFrozenPlayerActions(Player player, BlockPlaceEvent event) {
        if (isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private void handlePvPFreeze(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target && isFrozen(target)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMoveFreeze(PlayerMoveEvent event) {
        handleFrozenPlayerActions(event.getPlayer(), event);
    }

    @EventHandler
    public void onInteractFreeze(PlayerInteractEvent event) {
        handleFrozenPlayerActions(event.getPlayer(), event);
    }

    @EventHandler
    public void onBreakFreeze(BlockBreakEvent event) {
        handleFrozenPlayerActions(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlaceFreeze(BlockPlaceEvent event) {
        handleFrozenPlayerActions(event.getPlayer(), event);
    }

    @EventHandler
    public void onPvPFreeze(EntityDamageByEntityEvent event) {
        handlePvPFreeze(event);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tempban " + player.getName() + " 3d Leaving the server while frozen");
            unfreezePlayer(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isFrozen(player) && event.getFrom().distance(Objects.requireNonNull(event.getTo())) > 0) {
            event.setTo(event.getFrom());
        }
    }
}