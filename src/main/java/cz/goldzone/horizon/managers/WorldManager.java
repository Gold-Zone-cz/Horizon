package cz.goldzone.horizon.managers;

import org.bukkit.World;

import java.util.List;

public class WorldManager {
    private static final List<String> VALID_WORLD_NAMES = List.of("world", "world_nether", "world_the_end");

    public static boolean isValidSourceWorld(World world) {
        return world != null && VALID_WORLD_NAMES.contains(world.getName());
    }
}