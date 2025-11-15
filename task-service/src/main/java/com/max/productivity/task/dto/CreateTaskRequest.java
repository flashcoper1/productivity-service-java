package com.max.productivity.task.dto;

import java.time.Instant;

/**
 * Запрос на создание новой задачи.
 *
 * @param title заголовок задачи
 * @param priority приоритет задачи
 * @param dueDate срок выполнения задачи
 * @param creatorUserId идентификатор пользователя-создателя задачи
 */
public record CreateTaskRequest(
    String title,
    int priority,
    Instant dueDate,
    Long creatorUserId
) {
}

