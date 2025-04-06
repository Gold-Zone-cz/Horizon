package cz.goldzone.horizon;

import com.google.gson.Gson;
import cz.goldzone.horizon.admin.NetherListCommand;
import cz.goldzone.horizon.commands.HorizonCommand;
import cz.goldzone.horizon.commands.economy.BalTopCommand;
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
import cz.goldzone.horizon.listeners.JoinListener;
import cz.goldzone.horizon.listeners.TimeVoteListener;
import cz.goldzone.horizon.managers.*;
import cz.goldzone.horizon.placeholders.MoneyPlaceholders;
import cz.goldzone.horizon.placeholders.VotePlaceholders;
import cz.goldzone.neuron.shared.api.discord.webhook.WebhookClient;
import dev.digitality.digitalgui.DigitalGUI;
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

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        DigitalGUI.register(this);

        initializeManagers();
        registerCommands();
        registerListeners();
        registerPlaceholders();
        registerVault();
        initializeWebhookClient();

        getLogger().info("Horizon (" + getDescription().getVersion() + ") successfully loaded.");
    }

    private void initializeManagers() {
        HomesManager.createHomesTable();
        EconomyManager.createBalanceTable();
        PlayerWarpsManager.createPlayerWarpTable();
        FreezeManager.startTask();
        JailManager.startTask();
        VoteManager.loadVotes();
    }

    private void registerListeners() {
        Arrays.asList(new FreezeManager(), new JoinListener(), new ClickListener(), new TimeVoteListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VotePlaceholders().register();
            new MoneyPlaceholders().register();
            getLogger().info("PlaceholderAPI registered successfully, support enabled.");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will not work.");
        }
    }

    private void registerCommands() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        TeleportManager teleportManager = new TeleportManager();

        registerCommand(commands, "horizon", new HorizonCommand());
        registerCommand(commands, "netherlist", new NetherListCommand());
        registerCommand(commands, "warp", new WarpCommand());
        registerCommand(commands, "warps", new WarpsListCommand());
        registerCommand(commands, "setwarp", new SetWarpCommand());
        registerCommand(commands, "delwarp", new DelWarpCommand());
        registerCommand(commands, "playerwarps", new PlayerWarpsCommand());
        registerCommand(commands, "sethome", new SetHomeCommand());
        registerCommand(commands, "delhome", new DelHomeCommand());
        registerCommand(commands, "home", new HomeCommand());
        registerCommand(commands, "homes", new HomeListCommand());
        registerCommand(commands, "wb", new CraftCommand());
        registerCommand(commands, "enderchest", new EnderChestCommand());
        registerCommand(commands, "tpa", new TpaCommand(teleportManager));
        registerCommand(commands, "tpaccept", new TpaAcceptCommand(teleportManager));
        registerCommand(commands, "tpdeny", new TpaDenyCommand(teleportManager));
        registerCommand(commands, "i", new ItemCommand());
        registerCommand(commands, "balance", new BalanceCommand());
        registerCommand(commands, "anvil", new AnvilCommand());
        registerCommand(commands, "pay", new PayCommand());
        registerCommand(commands, "paytoggle", new TpToggleCommand());
        registerCommand(commands, "tptoggle", new TpToggleCommand());
        registerCommand(commands, "repair", new RepairCommand());
        registerCommand(commands, "hat", new HatCommand());
        registerCommand(commands, "tv", new TimeVoteCommand());
        registerCommand(commands, "freeze", new FreezeCommand());
        registerCommand(commands, "unfreeze", new UnFreezeCommand());
        registerCommand(commands, "rtp", new RandomTeleportCommand());
        registerCommand(commands, "setjail", new SetJailPlaceCommand());
        registerCommand(commands, "jail", new JailCommand());
        registerCommand(commands, "unjail", new UnJailCommand());
        registerCommand(commands, "baltop", new BalTopCommand());
        registerCommand(commands, "ratemessage", new RateMessageCommand());

        commands.forEach((cmd, executor) -> {
            if (getCommand(cmd) != null) {
                Objects.requireNonNull(getCommand(cmd)).setExecutor(executor);
                setTabCompleter(cmd);
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

    private void registerCommand(Map<String, CommandExecutor> commands, String commandName, CommandExecutor executor) {
        commands.put(commandName, executor);
    }

    private void registerVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            if (EconomyManager.setupEconomy()) {
                getLogger().info("Economy provider found and registered successfully.");
            } else {
                getLogger().warning("Economy provider not found. Economy features will not work.");
            }
        } else {
            getLogger().warning("Economy provider not found. Economy features will not work.");
        }
    }

    private void initializeWebhookClient() {
        String webhookUrl = ConfigManager.getConfig("config").getString("WebhookURL");
        if (webhookUrl != null) {
            webhookClient = WebhookClient.withUrl(webhookUrl);
            getLogger().info("Webhook client initialized successfully.");
        } else {
            getLogger().warning("Webhook URL is not configured!");
        }
    }
}