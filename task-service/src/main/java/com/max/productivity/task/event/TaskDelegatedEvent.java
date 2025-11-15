package com.max.productivity.task.event;
}
    }
        return newOwnerId;
    public Long getNewOwnerId() {
     */
     * @return ID нового владельца
     *
     * Возвращает идентификатор нового владельца задачи.
    /**

    }
        return previousOwnerId;
    public Long getPreviousOwnerId() {
     */
     * @return ID предыдущего владельца
     *
     * Возвращает идентификатор предыдущего владельца задачи.
    /**

    }
        return taskId;
    public Long getTaskId() {
     */
     * @return ID задачи
     *
     * Возвращает идентификатор делегированной задачи.
    /**

    }
        this.newOwnerId = newOwnerId;
        this.previousOwnerId = previousOwnerId;
        this.taskId = taskId;
        super(source);
    public TaskDelegatedEvent(Object source, Long taskId, Long previousOwnerId, Long newOwnerId) {
     */
     * @param newOwnerId идентификатор нового владельца задачи
     * @param previousOwnerId идентификатор предыдущего владельца задачи
     * @param taskId идентификатор делегированной задачи
     * @param source источник события
     *
     * Создаёт новое событие делегирования задачи.
    /**

    private final Long newOwnerId;
    private final Long previousOwnerId;
    private final Long taskId;

public class TaskDelegatedEvent extends ApplicationEvent {
 */
 * Событие, публикуемое при делегировании задачи другому пользователю.
/**

import org.springframework.context.ApplicationEvent;


