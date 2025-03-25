package cz.goldzone.horizon;

import com.google.gson.Gson;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.PlayerWarpsCommand;
import cz.goldzone.horizon.commands.admin.ItemCommand;
import cz.goldzone.horizon.commands.economy.BalanceCommand;
import cz.goldzone.horizon.commands.global.CraftCommand;
import cz.goldzone.horizon.commands.global.EnderChestCommand;
import cz.goldzone.horizon.commands.home.DelHomeCommand;
import cz.goldzone.horizon.commands.home.HomeCommand;
import cz.goldzone.horizon.commands.home.HomeListCommand;
import cz.goldzone.horizon.commands.home.SetHomeCommand;
import cz.goldzone.horizon.commands.player.TpaAcceptCommand;
import cz.goldzone.horizon.commands.player.TpaCommand;
import cz.goldzone.horizon.commands.player.TpaDenyCommand;
import cz.goldzone.horizon.commands.warp.WarpsListCommand;
import cz.goldzone.horizon.commands.warp.DelWarpCommand;
import cz.goldzone.horizon.commands.warp.SetWarpCommand;
import cz.goldzone.horizon.commands.warp.WarpCommand;
import cz.goldzone.horizon.managers.HomesManager;
import cz.goldzone.horizon.managers.MoneyManager;
import cz.goldzone.horizon.managers.TeleportManager;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import cz.goldzone.horizon.managers.ConfigManager;

import java.util.HashMap;
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
        MoneyManager.createBalanceTable();

        getLogger().info("Plugin successfully started with all configuration files loaded!");
    }

    private void registerCommands() {
        TeleportManager teleportManager = new TeleportManager();

        Map<String, CommandExecutor> commands = new HashMap<>();
        commands.put("horizon", new HorizonCommand());
        commands.put("warp", new WarpCommand());
        commands.put("warps", new WarpsListCommand());
        commands.put("setwarp", new SetWarpCommand());
        commands.put("delwarp", new DelWarpCommand());
        commands.put("pwarp", new PlayerWarpsCommand());
        commands.put("sethome", new SetHomeCommand());
        commands.put("delhome", new DelHomeCommand());
        commands.put("home", new HomeCommand());
        commands.put("homes", new HomeListCommand());
        commands.put("wb", new CraftCommand());
        commands.put("enderchest", new EnderChestCommand());
        commands.put("tpa", new TpaCommand(teleportManager));
        commands.put("tpaccept", new TpaAcceptCommand(teleportManager));
        commands.put("tpdeny", new TpaDenyCommand(teleportManager));
        commands.put("i", new ItemCommand());
        commands.put("balance", new BalanceCommand());

        commands.forEach((cmd, executor) -> {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(executor);
                getCommand(cmd).setTabCompleter(new FillTab());
            }
        });
    }
}
