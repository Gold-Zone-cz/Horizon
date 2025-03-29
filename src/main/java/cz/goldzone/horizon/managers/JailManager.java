package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.commands.admin.SetJailCommand;
import cz.goldzone.neuron.shared.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
                                new TextComponent("§7Jailed for §c" + jailTime.get(player) +
                                        " minutes §8| §7Reason: §c" + jailReason.get(player) +
                                        " §8| §7Jailed by: §c" + jailedBy.get(player)));
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    public static void unjail(Player player) {
        remove(player, false);

        FileConfiguration config = Main.getConfigManager().getConfig("jail.yml");
        String playerName = player.getName().toLowerCase();

        config.set("Jail.Time." + playerName, null);
        config.set("Jail.Staff." + playerName, null);
        config.set("Jail.Reason." + playerName, null);

        Main.getInstance().saveConfig();

        player.performCommand("spawn");
        player.sendMessage(Lang.getPrefix("Horizon") + "§aYou have been released from jail.");
        player.setGameMode(GameMode.SURVIVAL);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    public static void remove(Player player, boolean save) {
        if (save && isJailed(player)) {
            FileConfiguration config = Main.getConfigManager().getConfig("jail.yml");
            String playerName = player.getName().toLowerCase();

            config.set("Jail.Time." + playerName, jailTime.get(player));
            config.set("Jail.Staff." + playerName, jailedBy.get(player));
            config.set("Jail.Reason." + playerName, jailReason.get(player));
            Main.getInstance().saveConfig();
        }
        jailTime.remove(player);
        jailReason.remove(player);
        jailedBy.remove(player);
    }

    public static void check(Player player) {
        FileConfiguration config = Main.getConfigManager().getConfig("jail.yml");
        String playerName = player.getName().toLowerCase();
        if (!config.contains("Jail.Time." + playerName)) return;

        new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    jail(player, config.getInt("Jail.Time." + playerName),
                            config.getString("Jail.Reason." + playerName),
                            config.getString("Jail.Staff." + playerName));
                }
            }
        }.runTaskLater(Main.getInstance(), 100L);
    }

    public static void jail(Player target, int duration, String reason, String staff) {
        if (target == null) {
            Main.getInstance().getLogger().warning("Target player is null!");
            return;
        }

        remove(target, false);

        jailTime.put(target, duration);
        jailReason.put(target, reason);
        jailedBy.put(target, staff);

        FileConfiguration config = Main.getConfigManager().getConfig("jail.yml");
        String targetName = target.getName().toLowerCase();
        config.set("Jail.Time." + targetName, duration);
        config.set("Jail.Reason." + targetName, reason);
        config.set("Jail.Staff." + targetName, staff);
        Main.getInstance().saveConfig();

        checkInventory(target);
        target.teleport(SetJailCommand.getJailLocation());

        Location jailLocation = SetJailCommand.getJailLocation();
        if (jailLocation == null) {
            Main.getInstance().getLogger().warning("Jail location is not set! Please set it using /setjail command.");
            return;
        }

        target.setHealth(20);
        target.setFoodLevel(20);
        target.setGameMode(GameMode.ADVENTURE);
        target.sendMessage(Lang.getPrefix("Horizon") + "<red>You have been placed in jail! Reason: " + reason);
        target.sendTitle("<red><bold>JAIL", "<gray>You have been jailed!", 0, 50, 0);
        target.setAllowFlight(false);
        target.setFlying(false);
        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 1.0f);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        check(player);
    }
}


