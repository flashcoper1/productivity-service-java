package com.max.productivity.task.event;

import org.springframework.context.ApplicationEvent;

/**
 * Событие, публикуемое при завершении задачи.
 */
public class TaskCompletedEvent extends ApplicationEvent {

    private final Long taskId;

    /**
     * Создаёт новое событие завершения задачи.
     *
     * @param source источник события
     * @param taskId идентификатор завершённой задачи
     */
    public TaskCompletedEvent(Object source, Long taskId) {
        super(source);
        this.taskId = taskId;
    }

    /**
     * Возвращает идентификатор завершённой задачи.
     *
     * @return ID задачи
     */
    public Long getTaskId() {
        return taskId;
    }
}

