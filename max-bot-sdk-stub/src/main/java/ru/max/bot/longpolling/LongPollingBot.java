package ru.max.bot.longpolling;

import ru.max.botapi.client.MaxClient;

/**
 * Заглушка для LongPollingBot из Max Bot SDK.
 */
public class LongPollingBot {

    protected final MaxClient maxClient;

    public LongPollingBot(MaxClient maxClient) {
        this.maxClient = maxClient;
    }

    public void start() {
        // Заглушка - реализация будет в настоящем SDK
    }

    public void stop() {
        // Заглушка - реализация будет в настоящем SDK
    }
}


