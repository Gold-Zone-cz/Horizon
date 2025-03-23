package cz.goldzone.horizon;

import com.google.gson.Gson;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.PlayerWarpsCommand;
import cz.goldzone.horizon.commands.warp.WarpsListCommand;
import cz.goldzone.horizon.commands.warp.DelWarpCommand;
import cz.goldzone.horizon.commands.warp.SetWarpCommand;
import cz.goldzone.horizon.commands.warp.WarpCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import cz.goldzone.horizon.managers.ConfigManager;

import java.util.Map;

public final class Main extends JavaPlugin {

    @Getter
    private static final Gson gson = new Gson();
    @Getter
    private static Main instance;
    @Getter
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);

        registerCommands();

        getLogger().info("Plugin successfully started with all configuration files loaded!");
    }

    private void registerCommands() {
        Map.of(
                "horizon", new HorizonCommand(),
                "warp", new WarpCommand(),
                "warps", new WarpsListCommand(),
                "setwarp", new SetWarpCommand(),
                "delwarp", new DelWarpCommand()
        ).forEach((cmd, executor) -> {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(executor);
                getCommand(cmd).setTabCompleter(new FillTab());
            }
        });
    }
}
