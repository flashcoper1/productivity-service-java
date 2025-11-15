package com.max.productivity.gateway;

import com.max.productivity.common.dto.TaskDto;
import com.max.productivity.common.dto.UserDto;
import com.max.productivity.common.exception.TaskNotFoundException;
import com.max.productivity.identity.exception.UserNotFoundException;
import com.max.productivity.identity.service.IdentityService;
import com.max.productivity.task.dto.CreateTaskRequest;
import com.max.productivity.task.service.TaskService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.max.botapi.annotation.CommandHandler;
import ru.max.botapi.client.MaxClient;
import ru.max.botapi.model.Message;
import ru.max.botapi.model.NewMessageBody;
import ru.max.botapi.queries.SendMessageQuery;

import java.util.List;

/**
 * Контроллер для взаимодействия с ботом.
 * Обрабатывает команды от пользователей и управляет задачами.
 * Автоматически регистрирует пользователей при первой команде.
 */
@Component
@RestController
@RequestMapping("/api/bot")
public class MaxBotController {

    private final TaskService taskService;
    private final IdentityService identityService;
    private final MaxClient maxClient;

    public MaxBotController(TaskService taskService, IdentityService identityService, MaxClient maxClient) {
        this.taskService = taskService;
        this.identityService = identityService;
        this.maxClient = maxClient;
    }

    /**
     * Обрабатывает команду /addTask для создания новой задачи.
     *
     * @param message сообщение от пользователя, содержащее команду и текст задачи
     */
    @CommandHandler("/addTask")
    public void handleAddTask(Message message) {
        // Автоматическая регистрация пользователя
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        UserDto user = identityService.findOrCreateUser(messengerId, userName);

        // Извлекаем текст сообщения
        String messageText = message.getBody().getText();

        // Парсим название задачи (берем весь текст после команды "/addTask ")
        String taskTitle = extractTaskTitle(messageText);

        // Создаем запрос на создание задачи с использованием внутреннего ID пользователя
        CreateTaskRequest request = new CreateTaskRequest(
            taskTitle,
            0,  // priority по умолчанию
            null,  // dueDate пока не указываем
            user.id()  // Используем внутренний ID пользователя
        );

        // Создаем задачу
        TaskDto createdTask = taskService.createTask(request);

        // Отправляем подтверждение пользователю
        String responseText = "✅ Задача создана: " + createdTask.title();
        new SendMessageQuery(maxClient, new NewMessageBody(responseText))
            .userId(messengerId)
            .execute();
    }

    /**
     * Обрабатывает команду /myTasks для получения списка задач пользователя.
     *
     * @param message сообщение от пользователя с командой
     */
    @CommandHandler("/myTasks")
    public void handleGetMyTasks(Message message) {
        // Автоматическая регистрация пользователя
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        UserDto user = identityService.findOrCreateUser(messengerId, userName);

        // Получаем задачи пользователя по внутреннему ID
        List<TaskDto> tasks = taskService.getTasksForUser(user.id());

        // Создаем StringBuilder для форматирования ответа
        StringBuilder response = new StringBuilder();

        // Если список задач пуст
        if (tasks.isEmpty()) {
            response.append("У вас нет активных задач.");
        } else {
            // Если задачи есть, итерируемся по списку
            response.append("Ваши задачи:\n");
            for (TaskDto task : tasks) {
                response.append("• ")
                    .append(task.title())
                    .append(" (Приоритет: ")
                    .append(task.priority())
                    .append(")\n");
            }
        }

        // Отправляем отформатированное сообщение
        new SendMessageQuery(maxClient, new NewMessageBody(response.toString()))
            .userId(messengerId)
            .execute();
    }

    /**
     * Обрабатывает команду /delegate для делегирования задачи другому пользователю.
     * Формат команды: /delegate {taskId} {targetUserId}
     * Пример: /delegate 101 5005
     *
     * @param message сообщение от пользователя с командой
     */
    @CommandHandler("/delegate")
    public void handleDelegateTask(Message message) {
        // Автоматическая регистрация пользователя
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        UserDto user = identityService.findOrCreateUser(messengerId, userName);

        String messageText = message.getBody().getText();

        try {
            // Парсим taskId и targetUserId из текста команды "/delegate 101 5005"
            String[] parts = messageText.trim().split("\\s+");

            if (parts.length < 3) {
                String errorMsg = "Неверный формат команды. Используйте: /delegate {taskId} {targetUserId}";
                new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                    .userId(messengerId)
                    .execute();
                return;
            }

            Long taskId = Long.parseLong(parts[1]);
            Long targetUserId = Long.parseLong(parts[2]);

            // Вызываем сервис для делегирования задачи (используем внутренний ID)
            taskService.delegateTask(taskId, targetUserId);

            // Формируем сообщение об успехе
            String successMsg = "✅ Задача #" + taskId + " успешно делегирована пользователю #" + targetUserId;
            new SendMessageQuery(maxClient, new NewMessageBody(successMsg))
                .userId(messengerId)
                .execute();

        } catch (NumberFormatException e) {
            String errorMsg = "❌ Ошибка: ID задачи и ID пользователя должны быть числами";
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();

        } catch (TaskNotFoundException e) {
            String errorMsg = "❌ Задача не найдена: " + e.getMessage();
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();

        } catch (UserNotFoundException e) {
            String errorMsg = "❌ Пользователь не найден: " + e.getMessage();
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();

        } catch (Exception e) {
            String errorMsg = "❌ Произошла ошибка при делегировании задачи: " + e.getMessage();
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();
        }
    }

    /**
     * Обрабатывает команду /complete для завершения задачи.
     * Формат команды: /complete {taskId}
     * Пример: /complete 101
     *
     * @param message сообщение от пользователя с командой
     */
    @CommandHandler("/complete")
    public void handleCompleteTask(Message message) {
        // Автоматическая регистрация пользователя
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        UserDto user = identityService.findOrCreateUser(messengerId, userName);

        String messageText = message.getBody().getText();

        try {
            // Парсим taskId из текста команды "/complete 101"
            String[] parts = messageText.trim().split("\\s+");

            if (parts.length < 2) {
                String errorMsg = "Неверный формат команды. Используйте: /complete {taskId}";
                new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                    .userId(messengerId)
                    .execute();
                return;
            }

            Long taskId = Long.parseLong(parts[1]);

            // Вызываем сервис для завершения задачи
            taskService.completeTask(taskId);

            // Формируем сообщение об успехе
            String successMsg = "🎉 Задача #" + taskId + " успешно завершена!";
            new SendMessageQuery(maxClient, new NewMessageBody(successMsg))
                .userId(messengerId)
                .execute();

        } catch (NumberFormatException e) {
            String errorMsg = "❌ Ошибка: ID задачи должен быть числом";
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();

        } catch (TaskNotFoundException e) {
            String errorMsg = "❌ Задача не найдена: " + e.getMessage();
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();

        } catch (Exception e) {
            String errorMsg = "❌ Произошла ошибка при завершении задачи: " + e.getMessage();
            new SendMessageQuery(maxClient, new NewMessageBody(errorMsg))
                .userId(messengerId)
                .execute();
        }
    }

    /**
     * Извлекает название задачи из текста сообщения.
     *
     * @param messageText текст сообщения с командой
     * @return название задачи
     */
    private String extractTaskTitle(String messageText) {
        // Простая логика парсинга: берем все после команды "/addTask "
        if (messageText != null && messageText.startsWith("/addTask ")) {
            return messageText.substring("/addTask ".length()).trim();
        }
        return messageText != null ? messageText.trim() : "";
    }

    // ...existing code...

    /**
     * Получает все задачи пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список задач пользователя
     */
    @GetMapping("/tasks")
    public List<TaskDto> getAllTasks(@RequestParam Long userId) {
        return taskService.getTasksByUserId(userId);
    }

    /**
     * Создаёт новую задачу.
     *
     * @param request запрос на создание задачи
     * @return созданная задача
     */
    @PostMapping("/tasks")
    public TaskDto createTask(@RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    /**
     * Получает задачу по идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача
     */
    @GetMapping("/tasks/{id}")
    public TaskDto getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи
     * @param taskDto новые данные задачи
     * @return обновлённая задача
     */
    @PutMapping("/tasks/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    /**
     * Удаляет задачу.
     *
     * @param id идентификатор задачи
     */
    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}

