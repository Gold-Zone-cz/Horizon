package cz.goldzone.horizon.managers;

import dev.digitality.digitalconfig.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<String, Configuration> configs = new HashMap<>();
    private final String[] configFiles = {"warps.yml", "player_warps.yml", "votes.yml", "config.yml", "jail.yml"};

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Bukkit.getLogger().warning("[Horizon] Failed to create the data folder.");
            return;
        }

        for (String configFile : configFiles) {
            Configuration config = new Configuration(dataFolder + "/" + configFile);
            config.createDefault();
            configs.put(configFile, config);
        }
    }

    public Configuration getConfig(String fileName) {
        return configs.get(fileName);
    }

    public void saveConfig(String fileName) {
        Configuration config = configs.get(fileName);
        if (config != null) {
            config.save();
            Bukkit.getLogger().info("[Horizon] Saved " + fileName);
        }
    }

    public void reloadAllConfigs() {
        configs.clear();
        loadConfigs();
        Bukkit.getLogger().info("[Horizon] Reloaded all configuration files.");
    }
}