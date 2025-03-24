package cz.goldzone.horizon;

import com.google.gson.Gson;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.PlayerWarpsCommand;
import cz.goldzone.horizon.commands.home.DelHomeCommand;
import cz.goldzone.horizon.commands.home.HomeCommand;
import cz.goldzone.horizon.commands.home.HomeListCommand;
import cz.goldzone.horizon.commands.home.SetHomeCommand;
import cz.goldzone.horizon.commands.warp.WarpsListCommand;
import cz.goldzone.horizon.commands.warp.DelWarpCommand;
import cz.goldzone.horizon.commands.warp.SetWarpCommand;
import cz.goldzone.horizon.commands.warp.WarpCommand;
import cz.goldzone.horizon.managers.HomesManager;
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
        HomesManager.createHomesTable();

        getLogger().info("Plugin successfully started with all configuration files loaded!");
    }

    private void registerCommands() {
        Map.of(
                "horizon", new HorizonCommand(),
                "warp", new WarpCommand(),
                "warps", new WarpsListCommand(),
                "setwarp", new SetWarpCommand(),
                "delwarp", new DelWarpCommand(),
                "pwarp", new PlayerWarpsCommand(),
                "sethome", new SetHomeCommand(),
                "delhome", new DelHomeCommand(),
                "home", new HomeCommand(),
                "homes", new HomeListCommand()
        ).forEach((cmd, executor) -> {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(executor);
                getCommand(cmd).setTabCompleter(new FillTab());
            }
        });
    }
}
