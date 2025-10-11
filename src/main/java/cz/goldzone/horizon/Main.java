package cz.goldzone.horizon;

import cz.goldzone.horizon.admin.NetherListCommand;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.economy.BalTopCommand;
import cz.goldzone.horizon.commands.economy.PayToggleCommand;
import cz.goldzone.horizon.commands.player.*;
import cz.goldzone.horizon.commands.playerwarps.PlayerWarpsCommand;
import cz.goldzone.horizon.commands.admin.*;
import cz.goldzone.horizon.commands.economy.BalanceCommand;
import cz.goldzone.horizon.commands.economy.PayCommand;
import cz.goldzone.horizon.commands.global.*;
import cz.goldzone.horizon.commands.home.DelHomeCommand;
import cz.goldzone.horizon.commands.home.HomeCommand;
import cz.goldzone.horizon.commands.home.HomeListCommand;
import cz.goldzone.horizon.commands.home.SetHomeCommand;
import cz.goldzone.horizon.commands.warp.WarpsListCommand;
import cz.goldzone.horizon.commands.warp.DelWarpCommand;
import cz.goldzone.horizon.commands.warp.SetWarpCommand;
import cz.goldzone.horizon.commands.warp.WarpCommand;
import cz.goldzone.horizon.listeners.ClickListener;
import cz.goldzone.horizon.listeners.DeathListener;
import cz.goldzone.horizon.listeners.JoinListener;
import cz.goldzone.horizon.managers.*;
import cz.goldzone.horizon.placeholders.EconomyPlaceholder;
import cz.goldzone.horizon.placeholders.VotePlaceholder;
import dev.digitality.digitalgui.DigitalGUI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize();
        DigitalGUI.register(this);

        initializeManagers();
        registerCommands();
        registerListeners();
        registerPlaceholders();
        registerVault();
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

    private void registerListeners() {
        Arrays.asList(new FreezeManager(), new JoinListener(), new ClickListener(), new DeathListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VotePlaceholder().register();
            new EconomyPlaceholder().register();
            getLogger().info("PlaceholderAPI registered successfully, support enabled.");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will not work.");
        }
    }

    private void registerCommands() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        TeleportManager teleportManager = new TeleportManager();

        // --- Horizon ---
        register(commands, "horizon", new HorizonCommand());

        // --- Warps ---
        register(commands, "warp", new WarpCommand());
        register(commands, "warps", new WarpsListCommand());
        register(commands, "setwarp", new SetWarpCommand());
        register(commands, "delwarp", new DelWarpCommand());

        // --- Player Warps ---
        register(commands, "playerwarps", new PlayerWarpsCommand());

        // --- Homes ---
        register(commands, "sethome", new SetHomeCommand());
        register(commands, "delhome", new DelHomeCommand());
        register(commands, "home", new HomeCommand());
        register(commands, "homes", new HomeListCommand());

        // --- Economy ---
        register(commands, "balance", new BalanceCommand());
        register(commands, "baltop", new BalTopCommand());
        register(commands, "pay", new PayCommand());
        register(commands, "paytoggle", new PayToggleCommand());

        // --- Player ---
        register(commands, "tpa", new TpaCommand(teleportManager));
        register(commands, "tpaccept", new TpaAcceptCommand(teleportManager));
        register(commands, "tpdeny", new TpaDenyCommand(teleportManager));
        register(commands, "tptoggle", new TpToggleCommand());
        register(commands, "rtp", new RandomTeleportCommand());
        register(commands, "ratemessage", new RateMessageCommand());

        // --- Global ---
        register(commands, "wb", new CraftCommand());
        register(commands, "anvil", new AnvilCommand());
        register(commands, "enderchest", new EnderChestCommand());
        register(commands, "repair", new RepairCommand());
        register(commands, "hat", new HatCommand());
        register(commands, "timevote", new TimeVoteCommand());
        register(commands, "playerweather" , new PlayerWeatherCommand());
        register(commands, "back", new BackCommand());
        register(commands, "flyspeed", new FlySpeedCommand());
        register(commands, "feed", new FeedCommand());
        register(commands, "heal", new HealCommand());
        register(commands, "playtime", new PlayTimeCommand());
        register(commands, "god", new GodCommand());

        // --- Admin ---
        register(commands, "freeze", new FreezeCommand());
        register(commands, "unfreeze", new UnFreezeCommand());
        register(commands, "setjail", new SetJailPlaceCommand());
        register(commands, "jail", new JailCommand());
        register(commands, "unjail", new UnJailCommand());
        register(commands, "netherlist", new NetherListCommand());
        register(commands, "setspawnlocation", new SpawnLocationCommand());
        register(commands, "invsee", new InvseeCommand());

        commands.forEach((cmd, executor) -> {
            var c = getCommand(cmd);
            if (c != null) {
                c.setExecutor(executor);
                setTabCompleter(cmd);
            } else {
                getLogger().warning("Command " + cmd + " not found in plugin.yml.");
            }
        });
    }

    private void setTabCompleter(String command) {
        if (command.equals("i")) {
            Objects.requireNonNull(getCommand(command)).setTabCompleter(new FillTabItemManager());
        } else {
            Objects.requireNonNull(getCommand(command)).setTabCompleter(new FillTabManager());
        }
    }

    private void register(Map<String, CommandExecutor> commands, String commandName, CommandExecutor executor) {
        commands.put(commandName, executor);
    }

    private void registerVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("Economy provider not found. Economy features will not work.");
            return;
        }

        EconomyManager.register();
        boolean success = EconomyManager.setup(this);
        if (success) {
            getLogger().info("Vault and Economy provider found. Economy features enabled.");
        } else {
            getLogger().warning("No Economy provider found. Economy features will not work.");
        }
    }
}