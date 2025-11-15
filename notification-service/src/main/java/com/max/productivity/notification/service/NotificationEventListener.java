package com.max.productivity.notification.service;

import com.max.productivity.task.event.TaskCompletedEvent;
import com.max.productivity.task.event.TaskCreatedEvent;
import com.max.productivity.task.event.TaskDelegatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Слушатель событий для асинхронной обработки уведомлений.
 * Реагирует на события создания задач и отправляет уведомления пользователям.
 */
@Service
public class NotificationEventListener {

    /**
     * Обрабатывает событие создания задачи.
     * Извлекает ID задачи и отправляет уведомление пользователю.
     *
     * @param event событие создания задачи
     */
    @EventListener
    public void handleTaskCreated(TaskCreatedEvent event) {
        // ...existing code...
    }

    /**
     * Обрабатывает событие делегирования задачи другому пользователю.
     * Логирует информацию о делегировании и отправляет уведомления.
     *
     * @param event событие делегирования задачи
     */
    @EventListener
    public void handleTaskDelegated(TaskDelegatedEvent event) {
        // Извлекаем данные из события
        Long taskId = event.getTaskId();
        Long previousOwnerId = event.getPreviousOwnerId();
        Long newOwnerId = event.getNewOwnerId();

        // Логируем получение события
        System.out.println("Задача " + taskId + " делегирована пользователю " + newOwnerId);

        // TODO: Интеграция с max-bot-sdk для отправки уведомлений
        // Отправить уведомление новому владельцу
        // maxBotClient.sendMessage(
        //     newOwnerId,
        //     "📋 Вам делегирована задача #" + taskId
        // );

        // Отправить уведомление предыдущему владельцу
        // maxBotClient.sendMessage(
        //     previousOwnerId,
        //     "📤 Вы делегировали задачу #" + taskId + " пользователю #" + newOwnerId
        // );

        // Дополнительная информация для отладки
        System.out.println("Предыдущий владелец: " + previousOwnerId);
        System.out.println("Новый владелец: " + newOwnerId);
        System.out.println("Источник события: " + event.getSource().getClass().getSimpleName());
    }

    /**
     * Обрабатывает событие завершения задачи.
     * Логирует информацию о завершении и отправляет поздравительное уведомление.
     *
     * @param event событие завершения задачи
     */
    @EventListener
    public void handleTaskCompleted(TaskCompletedEvent event) {
        // Извлекаем taskId из события
        Long taskId = event.getTaskId();

        // Логируем получение события
        System.out.println("Задача " + taskId + " завершена");

        // TODO: Интеграция с max-bot-sdk для отправки поздравления
        // maxBotClient.sendMessage(
        //     ownerId,
        //     "🎉 Поздравляем! Задача #" + taskId + " успешно завершена!"
        // );

        // Дополнительная информация для отладки
        System.out.println("Источник события: " + event.getSource().getClass().getSimpleName());
        System.out.println("Временная метка: " + event.getTimestamp());
    }
}

