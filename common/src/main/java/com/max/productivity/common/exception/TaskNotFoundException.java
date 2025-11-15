package com.max.productivity.common.exception;

/**
 * Исключение, выбрасываемое когда задача не найдена.
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с указанным ID задачи.
     *
     * @param taskId идентификатор ненайденной задачи
     */
    public TaskNotFoundException(Long taskId) {
        super("Task with id " + taskId + " not found.");
    }

    /**
     * Создаёт исключение с пользовательским сообщением.
     *
     * @param message сообщение об ошибке
     */
    public TaskNotFoundException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

