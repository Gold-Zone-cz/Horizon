package cz.goldzone.horizon;

import cz.goldzone.horizon.managers.*;
import dev.digitality.digitalgui.DigitalGUI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize();
        DigitalGUI.register(this);
        new RegisterManager(this).registerAll();
        initializeManagers();
    }

    @Override
    public void onDisable() {
        WebhookManager.shutdown();
        ConfigManager.saveConfigs();
    }

    private void initializeManagers() {
        HomesManager.createHomesTable();
        PlayerWarpsManager.createPlayerWarpTable();
        WebhookManager.initialize();
        FreezeManager.startTask();
        JailManager.startTask();
        VoteManager.loadVotes();
        EconomyManager.createTable();
    }
}