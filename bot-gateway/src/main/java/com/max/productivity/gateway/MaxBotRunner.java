package com.max.productivity.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.max.bot.longpolling.LongPollingBot;
import ru.max.bot.longpolling.LongPollingBotOptions;
import ru.max.botapi.client.MaxClient;

/**
 * Spring-компонент для управления жизненным циклом Long Polling бота.
 * Запускает бота при старте приложения и корректно останавливает при завершении.
 */
@Slf4j
@Component
public class MaxBotRunner implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {

    private final MaxClient maxClient;
    private final MaxBotController maxBotController;
    private LongPollingBot bot;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param maxClient клиент Max Bot SDK
     * @param maxBotController контроллер для обработки команд бота
     */
    public MaxBotRunner(MaxClient maxClient, MaxBotController maxBotController) {
        this.maxClient = maxClient;
        this.maxBotController = maxBotController;
    }

    /**
     * Запускает Long Polling бота при старте приложения.
     * Метод вызывается автоматически Spring Boot после инициализации контекста.
     *
     * @param args аргументы командной строки
     * @throws Exception если возникла ошибка при запуске бота
     */
    @Override
    public void run(String... args) throws Exception {
        // Простая и чистая инициализация бота без анонимных классов и переопределений
        this.bot = new LongPollingBot(maxClient, LongPollingBotOptions.DEFAULT, maxBotController);
        bot.start();
        log.info("Long Polling Bot успешно запущен");
    }

    /**
     * Останавливает бота при закрытии контекста приложения.
     * Обеспечивает корректное завершение работы бота.
     *
     * @param event событие закрытия контекста
     */
    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        if (bot != null) {
            bot.stop();
            log.info("Long Polling Bot остановлен");
        }
    }
}
