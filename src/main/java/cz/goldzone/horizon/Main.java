package cz.goldzone.horizon;

import cz.goldzone.horizon.managers.*;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        initializeManagers();
    }

    @Override
    public void onDisable() {
        WebhookManager.shutdown();
        ConfigManager.saveConfigs();
    }

    private void initializeManagers() {
        new RegisterManager(this).registerAll();
        HomesManager.createTable();
        PlayerWarpsManager.createTable();
        EconomyManager.createTable();
        WebhookManager.initialize();
        ConfigManager.initialize();
        FreezeManager.startTask();
        JailManager.startTask();
    }
}