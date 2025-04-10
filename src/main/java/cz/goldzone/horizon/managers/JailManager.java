package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.admin.StaffNotify;
import cz.goldzone.horizon.commands.admin.SetJailPlaceCommand;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.spigot.managers.GodManager;
import dev.digitality.digitalconfig.config.Configuration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class JailManager implements Listener {
    private static final Map<Player, Integer> jailTime = new HashMap<>();
    private static final Map<Player, String> jailReason = new HashMap<>();
    private static final Map<Player, String> jailedBy = new HashMap<>();

    private static final String PREFIX = Lang.getPrefix("Horizon");
    private static final List<Material> BANNED_ITEMS = Arrays.asList(Material.CHORUS_FRUIT, Material.ARROW, Material.FIREWORK_ROCKET);

    public static boolean isJailed(Player player) {
        return jailTime.containsKey(player);
    }

    private static void checkInventory(Player player) {
        List<ItemStack> bannedItems = Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> BANNED_ITEMS.contains(item.getType()))
                .toList();

        for (ItemStack item : bannedItems) {
            player.getInventory().remove(item);
            player.sendMessage(PREFIX + "<red>Removed jail-banned items from your inventory.");
        }
    }

    public static void startTask() {
        scheduleJailTimeTask();
        scheduleActionBarUpdate();
    }

    private static void scheduleJailTimeTask() {
        new BukkitRunnable() {
            public void run() {
                Iterator<Map.Entry<Player, Integer>> iterator = jailTime.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Player, Integer> entry = iterator.next();
                    Player player = entry.getKey();
                    int time = entry.getValue();

                    if (time > 1) {
                        jailTime.put(player, time - 1);
                    } else {
                        iterator.remove();
                        unjail(player);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 1200L, 1200L);
    }

    private static void scheduleActionBarUpdate() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isJailed(player)) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent("<gray>Jailed for <red>" + jailTime.get(player) +
                                        " <gray>minutes <dark_gray>| <gray>Reason: <red>" + jailReason.get(player) +
                                        " <dark_gray>| <gray>Jailed by: <red>" + jailedBy.get(player)));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    public static void unjail(Player player) {
        remove(player, false);
        Configuration config = ConfigManager.getConfig("jail");
        String playerName = player.getName().toLowerCase();

        config.set("Jail." + playerName, null);
        config.save();

        Optional<Location> lastLocationOpt = Optional.ofNullable(config.get("Jail." + playerName + ".LastLocation", Location.class));
        lastLocationOpt.ifPresentOrElse(player::teleport, () -> player.performCommand("spawn"));

        player.sendMessage(PREFIX + "<green>You have been released from jail!");
        resetPlayerState(player);
    }

    public static void remove(Player player, boolean save) {
        if (save && isJailed(player)) {
            Configuration config = ConfigManager.getConfig("jail");
            String playerName = player.getName().toLowerCase();

            config.set("Jail." + playerName + ".Time", jailTime.get(player));
            config.set("Jail." + playerName + ".Reason", jailReason.get(player));
            config.set("Jail." + playerName + ".Staff", jailedBy.get(player));
            config.save();
        }

        jailTime.remove(player);
        jailReason.remove(player);
        jailedBy.remove(player);
    }

    public static void check(Player player) {
        Configuration config = ConfigManager.getConfig("jail");
        String playerName = player.getName().toLowerCase();

        if (!config.hasKey("Jail." + playerName)) return;

        Integer storedTime = config.get("Jail." + playerName + ".Time", Integer.class);
        if (storedTime == null || storedTime <= 0) return;

        new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    jail(
                            player,
                            storedTime,
                            config.getString("Jail." + playerName + ".Reason"),
                            config.getString("Jail." + playerName + ".Staff")
                    );
                }
            }
        }.runTaskLater(Main.getInstance(), 100L);
    }

    public static void jail(Player target, int duration, String reason, String staff) {
        if (target == null) {
            Main.getInstance().getLogger().warning("Target player is null!");
            return;
        }

        Configuration config = ConfigManager.getConfig("jail");
        String targetName = target.getName().toLowerCase();

        Location lastLocation = adjustLastLocation(target);
        remove(target, false);
        jailTime.put(target, duration);
        jailReason.put(target, reason);
        jailedBy.put(target, staff);

        config.set("Jail." + targetName + ".LastLocation", lastLocation);
        config.set("Jail." + targetName + ".Time", duration);
        config.set("Jail." + targetName + ".Reason", reason);
        config.set("Jail." + targetName + ".Staff", staff);
        config.save();

        WebhookManager.sendJailWebhook(target,
                String.valueOf(Objects.requireNonNull(target.getAddress()).getAddress()),
                Main.getInstance().getServer().getName(),
                staff, reason, duration);

        checkInventory(target);
        teleportToJail(target, reason);
        notifyStaff(staff, target, duration, reason);
    }

    private static Location adjustLastLocation(Player target) {
        Location lastLocation = target.getLocation();
        if (WorldManager.isValidSourceWorld(Objects.requireNonNull(lastLocation.getWorld()))) {
            return Optional.ofNullable(Bukkit.getWorld("world"))
                    .map(world -> Objects.requireNonNull(Bukkit.getWorld("Spawn")).getSpawnLocation())
                    .orElse(lastLocation);
        }
        return lastLocation;
    }

    private static void teleportToJail(Player target, String reason) {
        Location jailLocation = SetJailPlaceCommand.getJailLocation();
        if (jailLocation == null) {
            Main.getInstance().getLogger().warning("Jail location is not set! Use /setjail to set it.");
            return;
        }

        target.teleport(jailLocation);
        resetPlayerState(target);
        target.sendMessage(PREFIX + "<gray>You have been placed in jail! Reason: <red>" + reason);
        target.sendTitle("<red><bold>JAIL", "<gray>You have been jailed!", 0, 100, 0);
        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);
    }

    private static void notifyStaff(String staff, Player target, int duration, String reason) {
        Player staffPlayer = Bukkit.getPlayer(staff);
        if (staffPlayer != null) {
            String eventText = "JAIL: " + reason + " (" + duration + " minutes) -> " + target.getName();
            StaffNotify.setStaffNotify(staffPlayer, eventText);
        }
    }

    private static void resetPlayerState(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        GodManager.getGodList().remove(player);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
}