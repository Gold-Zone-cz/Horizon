package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import dev.digitality.digitalconfig.config.Configuration;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final Map<String, Configuration> configs = new HashMap<>();
    private static final String[] configFiles = {"warps", "config", "jail"};

    public static void initialize() {
        loadConfigs();
    }

    public static Configuration getConfig(String fileName) {
        return configs.get(fileName);
    }

    private static void loadConfigs() {
        if (Main.getInstance() == null) {
            Bukkit.getLogger().warning("[Horizon] Plugin not initialized.");
            return;
        }

        File dataFolder = Main.getInstance().getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Bukkit.getLogger().warning("[Horizon] Failed to create the data folder.");
            return;
        }

        for (String configFile : configFiles) {
            Configuration config = new Configuration(dataFolder + "/" + configFile + ".yml");
            config.createDefault();
            configs.put(configFile, config);
        }
    }

    public static void saveConfigs() {
        for (Configuration config : configs.values()) {
            config.save();
        }
    }

    public static void reloadAllConfigs() {
        configs.clear();
        loadConfigs();
        Bukkit.getLogger().info("[Horizon] Reloaded all configuration files.");
    }
}
