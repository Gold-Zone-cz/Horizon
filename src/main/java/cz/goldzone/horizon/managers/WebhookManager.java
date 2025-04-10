package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.api.discord.webhook.WebhookClient;
import cz.goldzone.neuron.shared.api.discord.webhook.send.WebhookEmbed;
import cz.goldzone.neuron.shared.api.discord.webhook.send.WebhookEmbedBuilder;
import cz.goldzone.neuron.shared.enums.Constants;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.time.Instant;

public class WebhookManager {

    private static WebhookClient webhookClient;

    public static void initialize() {
        String webhookUrl = ConfigManager.getConfig("config").getString("WebhookURL");

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            webhookClient = WebhookClient.withUrl(webhookUrl);
            Main.getInstance().getLogger().info("Webhook client initialized successfully.");
        } else {
            Main.getInstance().getLogger().warning("Webhook URL is not configured!");
        }
    }

    public static void sendMessage(String message) {
        if (webhookClient == null) {
            Main.getInstance().getLogger().warning("Webhook client is not initialized! Cannot send message.");
            return;
        }

        webhookClient.send(message);
    }

    public static void sendJailWebhook(Player playerName, String ip, String serverName, String action, String reason, int duration) {
        if (isInitialized()) {

            WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

            builder.setDescription(Lang.format(
                    "**Nickname:** `%{1}`\n**IP:** `%{2}`\n**Server:** `%{3}`\n**Action:** `%{4}`\n**Reason:** `%{5}`\n**Duration:** `%{6}` min",
                    playerName.getName(), ip, serverName, action, reason, String.valueOf(duration)
            ));

            builder.setTimestamp(Instant.now());
            builder.setFooter(new WebhookEmbed.EmbedFooter(playerName.getName(), "https://cravatar.eu/helmavatar/" + playerName));
            builder.setTitle(new WebhookEmbed.EmbedTitle(":newspaper: Logger", null));
            builder.setColor(Constants.COLOR_PRIMARY.getRGB());

            webhookClient.send(builder.build());
        }
    }

    public static void sendFreezeWebhook(Player playerName, InetAddress ip, String serverName, String action, int duration) {
        if (isInitialized()) {

            WebhookEmbedBuilder builder = new WebhookEmbedBuilder();

            builder.setDescription(Lang.format(
                    "**Nickname:** `%{1}`\n**IP:** `%{2}`\n**Server:** `%{3}`\n**Action:** `%{4}`\n**Duration:** `%{5}` min",
                    playerName.getName(), ip.getHostAddress(), serverName, action, String.valueOf(duration)
            ));

            builder.setTimestamp(Instant.now());
            builder.setFooter(new WebhookEmbed.EmbedFooter(playerName.getName(), "https://cravatar.eu/helmavatar/" + playerName));
            builder.setTitle(new WebhookEmbed.EmbedTitle(":newspaper: Logger", null));
            builder.setColor(Constants.COLOR_PRIMARY.getRGB());

            webhookClient.send(builder.build());
        }
    }


    public static void shutdown() {
        if (webhookClient != null) {
            webhookClient.close();
        }
    }

    public static boolean isInitialized() {
        return webhookClient != null;
    }
}
