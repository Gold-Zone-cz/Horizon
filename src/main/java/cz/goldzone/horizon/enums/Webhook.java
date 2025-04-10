package cz.goldzone.horizon.enums;

import lombok.Getter;

@Getter
public enum Webhook {

    ACTIONS("https://discord.com/api/webhooks/1354921414313906319/__Q7q-lT-zdzZDq8auKlBESvWd6QicBVsI8IqLlInKz_4G5BXQBgkTSaMB9FBEftF59P");

    private final String url;

    Webhook(String url) {
        this.url = url;
    }
}
