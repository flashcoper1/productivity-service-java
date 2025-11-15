package com.max.productivity.task.event;

import org.springframework.context.ApplicationEvent;

/**
 * Событие, публикуемое при создании новой задачи.
 */
public class TaskCreatedEvent extends ApplicationEvent {

    private final Long taskId;

    /**
     * Создаёт новое событие создания задачи.
     *
     * @param source источник события
     * @param taskId идентификатор созданной задачи
     */
    public TaskCreatedEvent(Object source, Long taskId) {
        super(source);
        this.taskId = taskId;
    }

    /**
     * Возвращает идентификатор созданной задачи.
     *
     * @return ID задачи
     */
    public Long getTaskId() {
        return taskId;
    }
}

