package com.max.productivity.task.event;

import org.springframework.context.ApplicationEvent;

/**
 * Событие, публикуемое при делегировании задачи другому пользователю.
 */
public class TaskDelegatedEvent extends ApplicationEvent {

    private final Long taskId;
    private final Long previousOwnerId;
    private final Long newOwnerId;

    /**
     * Создаёт новое событие делегирования задачи.
     *
     * @param source источник события
     * @param taskId идентификатор делегированной задачи
     * @param previousOwnerId идентификатор предыдущего владельца задачи
     * @param newOwnerId идентификатор нового владельца задачи
     */
    public TaskDelegatedEvent(Object source, Long taskId, Long previousOwnerId, Long newOwnerId) {
        super(source);
        this.taskId = taskId;
        this.previousOwnerId = previousOwnerId;
        this.newOwnerId = newOwnerId;
    }

    /**
     * Возвращает идентификатор делегированной задачи.
     *
     * @return ID задачи
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * Возвращает идентификатор предыдущего владельца задачи.
     *
     * @return ID предыдущего владельца
     */
    public Long getPreviousOwnerId() {
        return previousOwnerId;
    }

    /**
     * Возвращает идентификатор нового владельца задачи.
     *
     * @return ID нового владельца
     */
    public Long getNewOwnerId() {
        return newOwnerId;
    }
}
