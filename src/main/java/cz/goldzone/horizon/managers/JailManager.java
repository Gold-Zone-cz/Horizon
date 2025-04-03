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

    public static boolean isJailed(Player player) {
        return jailTime.containsKey(player);
    }

    private static void checkInventory(Player player) {
        List<Material> bannedItems = Arrays.asList(Material.CHORUS_FRUIT, Material.ARROW, Material.FIREWORK_ROCKET);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && bannedItems.contains(item.getType())) {
                player.getInventory().remove(item);
                player.sendMessage(Lang.getPrefix("Horizon") + "<red>Removed jail-banned items from your inventory.");
            }
        }
    }

    public static void startTask() {
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
        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");
        String playerName = player.getName().toLowerCase();

        config.set("Jail.Time." + playerName, null);
        config.set("Jail.Staff." + playerName, null);
        config.set("Jail.Reason." + playerName, null);
        config.save();

        Location lastLocation = config.get("Jail.LastLocation." + playerName, Location.class);
        if (lastLocation != null) {
            player.teleport(lastLocation);
        } else {
            player.performCommand("spawn");
        }

        player.sendMessage(Lang.getPrefix("Horizon") + "<green>You have been released from jail!");
        player.setGameMode(GameMode.SURVIVAL);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public static void remove(Player player, boolean save) {
        if (save && isJailed(player)) {
            Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");
            String playerName = player.getName().toLowerCase();

            config.set("Jail.Time." + playerName, jailTime.get(player));
            config.set("Jail.Staff." + playerName, jailedBy.get(player));
            config.set("Jail.Reason." + playerName, jailReason.get(player));
            config.save();
        }
        jailTime.remove(player);
        jailReason.remove(player);
        jailedBy.remove(player);
    }

    public static void check(Player player) {
        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");
        String playerName = player.getName().toLowerCase();

        if (config.get("Jail.Time." + playerName, null) == null) return;

        int storedTime = config.getInt("Jail.Time." + playerName);
        if (storedTime > 0) {
            new BukkitRunnable() {
                public void run() {
                    if (player.isOnline()) {
                        jail(player, storedTime,
                                config.getString("Jail.Reason." + playerName),
                                config.getString("Jail.Staff." + playerName));
                    }
                }
            }.runTaskLater(Main.getInstance(), 100L);
        }
    }

    public static void jail(Player target, int duration, String reason, String staff) {
        if (target == null) {
            Main.getInstance().getLogger().warning("Target player is null!");
            return;
        }

        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");
        Location lastLocation = target.getLocation();
        String targetName = target.getName().toLowerCase();

        remove(target, false);

        jailTime.put(target, duration);
        jailReason.put(target, reason);
        jailedBy.put(target, staff);

        config.set("Jail.LastLocation." + targetName, lastLocation);
        config.set("Jail.Time." + targetName, duration);
        config.set("Jail.Reason." + targetName, reason);
        config.set("Jail.Staff." + targetName, staff);
        config.save();

        checkInventory(target);

        Location jailLocation = SetJailPlaceCommand.getJailLocation();
        if (jailLocation == null) {
            Main.getInstance().getLogger().warning("Jail location is not set! Please set it using /setjail command.");
            return;
        }

        target.teleport(jailLocation);
        GodManager.getGodList().remove(target);
        target.setHealth(20);
        target.setFoodLevel(20);
        target.setGameMode(GameMode.ADVENTURE);
        target.sendMessage(Lang.getPrefix("Horizon") + "<gray>You have been placed in jail! Reason: <red>" + reason);
        target.sendTitle("<red><bold>JAIL", "<gray>You have been jailed!", 0, 100, 0);
        target.setAllowFlight(false);
        target.setFlying(false);
        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);

        String eventText = "JAIL: " + reason + " (" + duration + " minutes) -> " + target.getName();
        StaffNotify.setStaffNotify(Objects.requireNonNull(Bukkit.getPlayer(staff)), eventText);
    }
}



