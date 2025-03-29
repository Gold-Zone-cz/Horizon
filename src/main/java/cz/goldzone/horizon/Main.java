package cz.goldzone.horizon;

import com.google.gson.Gson;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.PlayerWarpsCommand;
import cz.goldzone.horizon.commands.admin.*;
import cz.goldzone.horizon.commands.economy.BalanceCommand;
import cz.goldzone.horizon.commands.economy.PayCommand;
import cz.goldzone.horizon.commands.global.*;
import cz.goldzone.horizon.commands.home.DelHomeCommand;
import cz.goldzone.horizon.commands.home.HomeCommand;
import cz.goldzone.horizon.commands.home.HomeListCommand;
import cz.goldzone.horizon.commands.home.SetHomeCommand;
import cz.goldzone.horizon.commands.player.TpToggleCommand;
import cz.goldzone.horizon.commands.player.TpaAcceptCommand;
import cz.goldzone.horizon.commands.player.TpaCommand;
import cz.goldzone.horizon.commands.player.TpaDenyCommand;
import cz.goldzone.horizon.commands.warp.WarpsListCommand;
import cz.goldzone.horizon.commands.warp.DelWarpCommand;
import cz.goldzone.horizon.commands.warp.SetWarpCommand;
import cz.goldzone.horizon.commands.warp.WarpCommand;
import cz.goldzone.horizon.listeners.JoinListener;
import cz.goldzone.horizon.managers.*;
import cz.goldzone.horizon.placeholders.MoneyPlaceholders;
import cz.goldzone.horizon.placeholders.VotePlaceholders;
import cz.goldzone.horizon.timevote.TimeVoteCommand;
import cz.goldzone.neuron.shared.api.discord.webhook.WebhookClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class Main extends JavaPlugin {

    @Getter
    private static WebhookClient webhookClient;
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
        registerListeners();
        registerPlaceholders();
        registerVault();

        HomesManager.createHomesTable();
        MoneyManager.createBalanceTable();
        FreezeManager.startTask();
        JailManager.startTask();
        VoteManager.loadVotes();

        loadWebhook();

        getLogger().info("Horizon successfully loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Horizon successfully unloaded.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new FreezeManager(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VotePlaceholders().register();
            new MoneyPlaceholders().register();
            getLogger().info("PlaceholderAPI support enabled.");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will not work.");
        }
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
        commands.put("anvil", new AnvilCommand());
        commands.put("pay", new PayCommand());
        commands.put("tptoggle", new TpToggleCommand());
        commands.put("repair", new RepairCommand());
        commands.put("hat", new HatCommand());
        commands.put("tv", new TimeVoteCommand());
        commands.put("freeze", new FreezeCommand());
        commands.put("unfreeze", new FreezeCommand());
        commands.put("rtp", new RandomTeleportCommand());
        commands.put("setjail", new SetJailCommand());
        commands.put("jail", new JailCommand());
        commands.put("unjail", new UnJailCommand());


        commands.forEach((cmd, executor) -> {
            if (getCommand(cmd) != null) {
                Objects.requireNonNull(getCommand(cmd)).setExecutor(executor);

                if (cmd.equals("i")) {
                    Objects.requireNonNull(getCommand(cmd)).setTabCompleter(new FillTabItemManager());
                } else {
                    Objects.requireNonNull(getCommand(cmd)).setTabCompleter(new FillTabManager());
                }
            }
        });
    }

    private void registerVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            getLogger().info("Vault found. Economy features will work.");
        } else {
            getLogger().warning("Vault not found. Economy features will not work.");
        }
    }

    private static void loadWebhook() {
        webhookClient = WebhookClient.withUrl(Objects.requireNonNull(configManager.getConfig("config.yml").getString("webhook_url")));
        getInstance().getLogger().info("Webhook client initialized successfully.");
    }
}