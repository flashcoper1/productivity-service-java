package com.max.productivity.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.max.botapi.client.MaxClient;

/**
 * Конфигурационный класс для настройки Max Bot SDK.
 * Предоставляет MaxClient как Spring Bean для использования во всём приложении.
 */
@Configuration
public class MaxBotConfig {

    /**
     * Создаёт и настраивает клиента Max Bot.
     * Токен бота считывается из конфигурации (application.yml).
     *
     * @param token токен бота из свойства max-bot.token
     * @return настроенный экземпляр MaxClient
     */
    @Bean
    public MaxClient maxClient(@Value("${max-bot.token}") String token) {
        return MaxClient.create(token);
    }
}

