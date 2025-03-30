package cz.goldzone.horizon.managers;

import org.bukkit.World;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WorldManager {

    private static final Set<String> VALID_WORLD_NAMES = new HashSet<>(Arrays.asList("world", "world_the_end", "world_nether"));

    public static boolean isValidSourceWorld(World world) {
        return VALID_WORLD_NAMES.contains(world.getName());
    }
}
