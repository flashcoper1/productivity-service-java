package ru.max.botapi.client;

/**
 * Заглушка для MaxClient из Max Bot SDK.
 * Используется для сборки проекта без установленного SDK.
 */
public class MaxClient {

    private final String token;

    private MaxClient(String token) {
        this.token = token;
    }

    /**
     * Создает экземпляр MaxClient с указанным токеном.
     *
     * @param token токен бота
     * @return экземпляр MaxClient
     */
    public static MaxClient create(String token) {
        return new MaxClient(token);
    }

    /**
     * Получает токен бота.
     *
     * @return токен бота
     */
    public String getToken() {
        return token;
    }
}

