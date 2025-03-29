package cz.goldzone.horizon.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;
import java.util.stream.Collectors;

public class WorldManager {

    public static List<World> getValidSourceWorlds() {
        return Bukkit.getWorlds().stream()
                .filter(world -> world.getName().equals("world") ||
                        world.getName().equals("world_the_end") ||
                        world.getName().equals("world_nether"))
                .collect(Collectors.toList());
    }

    public static boolean isValidSourceWorld(World world) {
        return !getValidSourceWorlds().contains(world);
    }

    public static boolean IsValidRTPWorld(World world) {
        return getValidSourceWorlds().contains(world);
    }
}
