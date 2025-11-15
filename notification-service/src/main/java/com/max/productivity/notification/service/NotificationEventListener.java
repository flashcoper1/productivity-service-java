package com.max.productivity.notification.service;

import com.max.productivity.common.dto.TaskDto;
import com.max.productivity.common.dto.UserDto;
import com.max.productivity.identity.service.IdentityService;
import com.max.productivity.task.event.TaskCompletedEvent;
import com.max.productivity.task.event.TaskCreatedEvent;
import com.max.productivity.task.event.TaskDelegatedEvent;
import com.max.productivity.task.service.TaskService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.max.botapi.client.MaxClient;
import ru.max.botapi.model.NewMessageBody;
import ru.max.botapi.queries.SendMessageQuery;

import java.util.Optional;

/**
 * Слушатель событий для асинхронной обработки уведомлений.
 * Реагирует на события создания задач и отправляет уведомления пользователям.
 */
@Service
public class NotificationEventListener {

    private final MaxClient maxClient;
    private final IdentityService identityService;
    private final TaskService taskService;

    public NotificationEventListener(MaxClient maxClient, IdentityService identityService, TaskService taskService) {
        this.maxClient = maxClient;
        this.identityService = identityService;
        this.taskService = taskService;
    }

    /**
     * Обрабатывает событие создания задачи.
     * Извлекает ID задачи и отправляет уведомление пользователю.
     *
     * @param event событие создания задачи
     */
    @EventListener
    public void handleTaskCreated(TaskCreatedEvent event) {
        Long taskId = event.getTaskId();

        // Получаем информацию о задаче
        TaskDto task = taskService.getTaskById(taskId);

        // Получаем владельца задачи
        Long ownerId = task.assignedToUserId();
        Optional<UserDto> userOpt = identityService.findUserById(ownerId);

        if (userOpt.isPresent()) {
            UserDto user = userOpt.get();
            String message = "✅ Задача создана: " + task.title();

            new SendMessageQuery(maxClient, new NewMessageBody(message))
                .userId(user.messengerId())
                .execute();
        }
    }

    /**
     * Обрабатывает событие делегирования задачи другому пользователю.
     * Логирует информацию о делегировании и отправляет уведомления.
     *
     * @param event событие делегирования задачи
     */
    @EventListener
    public void handleTaskDelegated(TaskDelegatedEvent event) {
        Long taskId = event.getTaskId();
        Long previousOwnerId = event.getPreviousOwnerId();
        Long newOwnerId = event.getNewOwnerId();

        // Отправляем уведомление новому владельцу
        Optional<UserDto> newOwnerOpt = identityService.findUserById(newOwnerId);
        if (newOwnerOpt.isPresent()) {
            UserDto newOwner = newOwnerOpt.get();
            String message = "📋 Вам делегирована задача #" + taskId;

            new SendMessageQuery(maxClient, new NewMessageBody(message))
                .userId(newOwner.messengerId())
                .execute();
        }

        // Отправляем уведомление предыдущему владельцу
        Optional<UserDto> previousOwnerOpt = identityService.findUserById(previousOwnerId);
        if (previousOwnerOpt.isPresent()) {
            UserDto previousOwner = previousOwnerOpt.get();
            String message = "📤 Вы делегировали задачу #" + taskId + " пользователю #" + newOwnerId;

            new SendMessageQuery(maxClient, new NewMessageBody(message))
                .userId(previousOwner.messengerId())
                .execute();
        }
    }

    /**
     * Обрабатывает событие завершения задачи.
     * Логирует информацию о завершении и отправляет поздравительное уведомление.
     *
     * @param event событие завершения задачи
     */
    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        Long taskId = event.getTaskId();

        // Получаем информацию о задаче
        TaskDto task = taskService.getTaskById(taskId);

        // Получаем владельца задачи
        Long ownerId = task.assignedToUserId();
        Optional<UserDto> userOpt = identityService.findUserById(ownerId);

        if (userOpt.isPresent()) {
            UserDto user = userOpt.get();
            String message = "🎉 Поздравляем! Задача #" + taskId + " '" + task.title() + "' успешно завершена!";

            new SendMessageQuery(maxClient, new NewMessageBody(message))
                .userId(user.messengerId())
                .execute();
        }
    }
}

