package com.max.productivity.common.dto;

/**
 * DTO для безопасной передачи данных о пользователе между модулями.
 * Использует Java Record для неизменяемости и компактности.
 */
public record UserDto(
    /**
     * Внутренний идентификатор пользователя в системе.
     */
    Long id,

    /**
     * Идентификатор пользователя из мессенджера.
     */
    Long messengerId,

    /**
     * Имя пользователя.
     */
    String userName
) {
}

