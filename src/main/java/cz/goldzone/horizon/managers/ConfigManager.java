package cz.goldzone.horizon.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final String[] configFiles = {"warps.yml", "player_warps.yml"};

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Bukkit.getLogger().severe("[Horizon] Failed to create the data folder.");
            return;
        }

        for (String fileName : configFiles) {
            File file = new File(dataFolder, fileName);
            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        Bukkit.getLogger().info("[Horizon] Created " + fileName);
                    } else {
                        Bukkit.getLogger().severe("[Horizon] Failed to create " + fileName);
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().severe("[Horizon] Error while creating " + fileName + ": " + e.getMessage());
                }
            }
            configs.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
    }

    public FileConfiguration getConfig(String fileName) {
        return configs.get(fileName);
    }

    public void saveConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        FileConfiguration config = configs.get(fileName);
        if (config != null) {
            try {
                config.save(file);
                Bukkit.getLogger().info("[Horizon] Saved " + fileName);
            } catch (IOException e) {
                Bukkit.getLogger().severe("[Horizon] Failed to save " + fileName + ": " + e.getMessage());
            }
        }
    }

    public void reloadAllConfigs() {
        configs.clear();
        loadConfigs();
        Bukkit.getLogger().info("[Horizon] Reloaded all configuration files.");
    }
}