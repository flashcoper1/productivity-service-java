package com.max.productivity.common.dto;

import java.time.Instant;

/**
 * DTO для передачи данных о задаче.
 *
 * @param id идентификатор задачи
 * @param title заголовок задачи
 * @param description описание задачи
 * @param status статус задачи (например, "TODO", "IN_PROGRESS", "DONE")
 * @param priority приоритет задачи
 * @param dueDate срок выполнения задачи
 * @param assignedToUserId идентификатор пользователя, которому назначена задача
 */
public record TaskDto(
    Long id,
    String title,
    String description,
    String status,
    int priority,
    Instant dueDate,
    Long assignedToUserId
) {
}

