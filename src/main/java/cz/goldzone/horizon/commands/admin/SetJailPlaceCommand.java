package cz.goldzone.horizon.commands.admin;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import dev.digitality.digitalconfig.config.Configuration;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SetJailPlaceCommand implements CommandExecutor {
    private static final Set<UUID> confirmationSet = new HashSet<>();

    @Getter
    private static Location jailLocation;

    public static boolean isSet() {
        return jailLocation != null;
    }

    public static void set(Location loc) {
        jailLocation = loc;
    }

    public static Location get() {
        return jailLocation;
    }

    public static void load() {
        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");
        if (config.getSection("JailPlace.World") != null) {
            World world = Bukkit.getWorld(Objects.requireNonNull(config.getString("JailPlace.World")));
            if (world == null) return;

            double x = config.getDouble("JailPlace.X");
            double y = config.getDouble("JailPlace.Y");
            double z = config.getDouble("JailPlace.Z");
            float yaw = (float) config.getDouble("JailPlace.Yaw");
            float pitch = (float) config.getDouble("JailPlace.Pitch");

            jailLocation = new Location(world, x, y, z, yaw, pitch);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.get("core.only_pl", sender));
            return true;
        }

        if (!player.hasPermission("horizon.admin.jail")) {
            player.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<gray>Usage: <red>/setjail");
            return false;
        }

        Configuration config = new Configuration(Main.getInstance().getDataFolder() + "/jail.yml");

        if (config.getSection("JailPlace.World") != null && !confirmationSet.contains(player.getUniqueId())) {
            player.sendMessage(Lang.getPrefix("Horizon") + "<red>Jail location already exists!\n <gray>Retype the command to overwrite it.");
            confirmationSet.add(player.getUniqueId());
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> confirmationSet.remove(player.getUniqueId()), 600L);
            return true;
        } else {
            confirmationSet.remove(player.getUniqueId());
        }

        Location loc = player.getLocation();
        config.set("JailPlace.World", Objects.requireNonNull(loc.getWorld()).getName());
        config.set("JailPlace.X", loc.getX());
        config.set("JailPlace.Y", loc.getY());
        config.set("JailPlace.Z", loc.getZ());
        config.set("JailPlace.Yaw", loc.getYaw());
        config.set("JailPlace.Pitch", loc.getPitch());
        config.save();

        set(loc);
        load();

        player.sendMessage(Lang.getPrefix("Jail") + "<gray>Location has been set!");
        return true;
    }
}
