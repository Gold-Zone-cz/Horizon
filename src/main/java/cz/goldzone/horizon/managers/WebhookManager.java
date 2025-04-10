package cz.goldzone.horizon.managers;

import cz.goldzone.horizon.Main;
import cz.goldzone.horizon.enums.Webhook;
import cz.goldzone.neuron.shared.Lang;
import cz.goldzone.neuron.shared.api.discord.webhook.WebhookClient;
import cz.goldzone.neuron.shared.api.discord.webhook.send.WebhookEmbed;
import cz.goldzone.neuron.shared.api.discord.webhook.send.WebhookEmbedBuilder;
import cz.goldzone.neuron.shared.api.discord.webhook.send.WebhookMessageBuilder;
import cz.goldzone.neuron.shared.enums.Constants;
import org.bukkit.entity.Player;

import java.time.Instant;

public class WebhookManager {

    private static WebhookClient webhookClient;

    public static void initialize() {
        String url = Webhook.ACTIONS.getUrl();

        if (url == null || url.isBlank()) {
            log("Webhook URL is not configured!", true);
            return;
        }

        webhookClient = WebhookClient.withUrl(url);
        new WebhookMessageBuilder()
                .setUsername(Constants.SERVER_NAME)
                .setAvatarUrl(Constants.SERVER_ICON);

        log("Webhook client initialized successfully.", false);
    }

    public static void sendMessage(String message) {
        if (isNotInitialized()) return;
        webhookClient.send(message);
    }

    public static void sendJailWebhook(
            Player player, String ip, String server, String action, String reason, int duration, String staff) {
        if (isNotInitialized()) return;

        WebhookEmbedBuilder embed = buildEmbed(
                player, ip, server, action, reason, duration, staff, true);
        webhookClient.send(embed.build());
    }

    public static void sendFreezeWebhook(
            Player player, String ip, String server, String action, Integer duration, String staff) {
        if (isNotInitialized()) return;

        WebhookEmbedBuilder embed = buildEmbed(
                player, ip, server, action, null, duration, staff, false);
        webhookClient.send(embed.build());
    }

    private static WebhookEmbedBuilder buildEmbed(Player player, String ip, String server, String action,
                                                  String reason, Integer duration, String staff, boolean includeReason) {

        String durationText = formatDuration(duration);

        String description = includeReason
                ? Lang.format("**Target:** `%{1}`\n**IP:** `%{2}`\n**Server:** `%{3}`\n**Action:** `%{4}`\n**Reason:** `%{5}`\n**Duration:** `%{6}`\n\n**Staff:** `%{7}`",
                player.getName(), ip, server, action, reason, durationText, staff)
                : Lang.format("**Target:** `%{1}`\n**IP:** `%{2}`\n**Server:** `%{3}`\n**Action:** `%{4}`\n**Duration:** `%{5}`\n\n**Staff:** `%{6}`",
                player.getName(), ip, server, action, durationText, staff);

        return new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(":newspaper2: Logger", null))
                .setDescription(description)
                .setColor(Constants.COLOR_PRIMARY.getRGB())
                .setTimestamp(Instant.now())
                .setFooter(new WebhookEmbed.EmbedFooter(player.getName(), getAvatarUrl(player.getName())));
    }

    private static String formatDuration(Integer duration) {
        if (duration == null || duration <= 0) return "0m";

        int hours = duration / 60;
        int minutes = duration % 60;

        return (hours > 0)
                ? Lang.format("%{1}h %{2}m", String.valueOf(hours), String.valueOf(minutes))
                : Lang.format("%{1}m", String.valueOf(minutes));
    }

    private static String getAvatarUrl(String playerName) {
        return "https://cravatar.eu/helmavatar/" + playerName;
    }

    private static void log(String message, boolean warn) {
        if (warn) {
            Main.getInstance().getLogger().warning(message);
        } else {
            Main.getInstance().getLogger().info(message);
        }
    }

    private static boolean isNotInitialized() {
        if (webhookClient == null) {
            log("Webhook client is not initialized! Cannot send message.", true);
            return false;
        }
        return true;
    }

    public static void shutdown() {
        if (webhookClient != null) {
            webhookClient.close();
        }
    }
}
