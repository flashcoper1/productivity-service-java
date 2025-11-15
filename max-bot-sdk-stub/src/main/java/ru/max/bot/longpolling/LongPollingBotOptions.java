package ru.max.bot.longpolling;

import ru.max.botapi.client.MaxClient;

/**
 * Заглушка для LongPollingBotOptions из Max Bot SDK.
 */
public class LongPollingBotOptions {

    private MaxClient client;
    private Object controller;

    public static LongPollingBotOptions builder() {
        return new LongPollingBotOptions();
    }

    public LongPollingBotOptions client(MaxClient client) {
        this.client = client;
        return this;
    }

    public LongPollingBotOptions controller(Object controller) {
        this.controller = controller;
        return this;
    }

    public LongPollingBot build() {
        return new LongPollingBot(client);
    }
}

