package com.max.productivity.task.service;

import com.max.productivity.common.dto.TaskDto;
import com.max.productivity.task.dto.CreateTaskRequest;

import java.util.List;

/**
 * Сервис для управления задачами.
 */
public interface TaskService {

    /**
     * Создаёт новую задачу на основе предоставленного запроса.
     *
     * @param request запрос на создание задачи, содержащий заголовок, приоритет,
     *                срок выполнения и идентификатор создателя
     * @return DTO созданной задачи с заполненными полями id, статусом и датой создания
     */
    TaskDto createTask(CreateTaskRequest request);

    /**
     * Получает задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return DTO задачи
     * @throws com.max.productivity.common.exception.TaskNotFoundException если задача не найдена
     */
    TaskDto getTaskById(Long id);

    /**
     * Получает все задачи указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO задач пользователя
     */
    List<TaskDto> getTasksByUserId(Long userId);

    /**
     * Получает список задач для указанного пользователя.
     * Возвращает список DTO задач, назначенных пользователю с указанным ID.
     *
     * @param userId идентификатор пользователя, для которого нужно получить задачи
     * @return список DTO задач для указанного пользователя
     */
    List<TaskDto> getTasksForUser(Long userId);

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи для обновления
     * @param taskDto DTO с новыми данными задачи
     * @param requesterId идентификатор пользователя, выполняющего обновление
     * @return обновлённое DTO задачи
     * @throws com.max.productivity.common.exception.TaskNotFoundException если задача не найдена
     */
    TaskDto updateTask(Long id, TaskDto taskDto, Long requesterId);

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи для удаления
     * @param requesterId идентификатор пользователя, выполняющего удаление
     * @throws com.max.productivity.common.exception.TaskNotFoundException если задача не найдена
     */
    void deleteTask(Long id, Long requesterId);

    /**
     * Делегирует задачу другому пользователю.
     * Передаёт владение задачей от текущего владельца новому пользователю.
     * Публикует событие TaskDelegatedEvent после успешного делегирования.
     *
     * @param taskId идентификатор задачи для делегирования
     * @param targetUserId идентификатор пользователя, которому делегируется задача
     * @param requesterId идентификатор пользователя, выполняющего делегирование
     * @throws com.max.productivity.common.exception.TaskNotFoundException если задача не найдена
     * @throws com.max.productivity.identity.exception.UserNotFoundException если целевой пользователь не найден
     */
    void delegateTask(Long taskId, Long targetUserId, Long requesterId)
        throws com.max.productivity.common.exception.TaskNotFoundException,
               com.max.productivity.identity.exception.UserNotFoundException;

    /**
     * Завершает выполнение задачи.
     * Устанавливает статус задачи в "COMPLETED" и публикует событие TaskCompletedEvent.
     *
     * @param taskId идентификатор задачи для завершения
     * @param requesterId идентификатор пользователя, выполняющего завершение задачи
     * @throws com.max.productivity.common.exception.TaskNotFoundException если задача не найдена
     */
    void completeTask(Long taskId, Long requesterId)
        throws com.max.productivity.common.exception.TaskNotFoundException;
}

