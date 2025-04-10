package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.admin.StaffNotify;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.player.GamePlayer;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FreezeManager implements Listener {
    private static final Map<Player, Integer> frozenPlayers = new HashMap<>();
    private static final Map<Player, String> freezingStaff = new HashMap<>();

    private static final String PREFIX = Lang.getPrefix("Horizon");

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
        GamePlayer gamePlayer = GamePlayer.get(player.getName());
        if (isFrozen(player)) {
            frozenPlayers.remove(player);
            freezingStaff.remove(player);
            player.sendMessage(PREFIX + "<gray>You have been unfrozen!");
            player.sendTitle("<green><bold>UNFROZEN!", "<gray>You have been unfrozen.", 0, 100, 0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            WebhookManager.sendFreezeWebhook(player,
                    gamePlayer.getLastIP(),
                    gamePlayer.getServer(),
                    "UNFREEZE",
                    null,
                    freezingStaff.get(player));
        }
    }

    public static void freezePlayer(Player player, int minutes, String staff) {
        GamePlayer gamePlayer = GamePlayer.get(player.getName());

        if (isFrozen(player)) {
            return;
        }

        frozenPlayers.put(player, minutes);
        freezingStaff.put(player, staff);

        player.setHealth(20);
        player.setFoodLevel(20);

        player.sendMessage(PREFIX + "<gray>You have been frozen for <red>" + minutes + " <gray>minutes!");
        player.sendTitle("<red><bold>FROZEN!", "<gray>You have been frozen for " + minutes + " minutes.", 0, 50, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);

        WebhookManager.sendFreezeWebhook(player, gamePlayer.getLastIP(), gamePlayer.getServer(), "FREEZE", minutes, staff);
        logFreezeEvent(player, staff);
    }

    private static void logFreezeEvent(Player player, String staff) {
        String eventText = "EXECUTED FREEZE BY " + staff;
        Player staffPlayer = Bukkit.getPlayer(staff);
        if (staffPlayer != null) {
            StaffNotify.setStaffNotify(player, eventText);
        }
    }

    private void handleFrozenPlayerActions(Player player, PlayerMoveEvent event) {
        event.setCancelled(isFrozen(player));
    }

    private void handleFrozenPlayerActions(Player player, PlayerInteractEvent event) {
        event.setCancelled(isFrozen(player));
    }

    private void handleFrozenPlayerActions(Player player, BlockBreakEvent event) {
        event.setCancelled(isFrozen(player));
    }

    private void handleFrozenPlayerActions(Player player, BlockPlaceEvent event) {
        event.setCancelled(isFrozen(player));
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