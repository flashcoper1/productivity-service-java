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
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        try {
            UserDto user = identityService.findOrCreateUser(messengerId, userName);

            String messageText = message.getBody().getText();
            String taskTitle = extractTaskTitle(messageText);

            CreateTaskRequest request = new CreateTaskRequest(
                taskTitle,
                0,
                null,
                user.id()
            );

            taskService.createTask(request);
        } catch (TaskNotFoundException e) {
            sendMessage(messengerId, "❌ Задача не найдена: " + e.getMessage());
        } catch (UserNotFoundException e) {
            sendMessage(messengerId, "❌ Пользователь не найден: " + e.getMessage());
        } catch (SecurityException e) {
            sendMessage(messengerId, "⛔ Недостаточно прав: " + e.getMessage());
        } catch (Exception e) {
            sendMessage(messengerId, "❌ Произошла ошибка при создании задачи: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
        }
    }

    /**
     * Обрабатывает команду /myTasks для получения списка задач пользователя.
     *
     * @param message сообщение от пользователя с командой
     */
    @CommandHandler("/myTasks")
    public void handleGetMyTasks(Message message) {
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        try {
            UserDto user = identityService.findOrCreateUser(messengerId, userName);

            List<TaskDto> tasks = taskService.getTasksForUser(user.id());

            StringBuilder response = new StringBuilder();
            if (tasks.isEmpty()) {
                response.append("У вас нет активных задач.");
            } else {
                response.append("Ваши задачи:\n");
                for (TaskDto task : tasks) {
                    response.append("• ")
                        .append(task.title())
                        .append(" (Приоритет: ")
                        .append(task.priority())
                        .append(")\n");
                }
            }

            sendMessage(messengerId, response.toString());
        } catch (TaskNotFoundException e) {
            sendMessage(messengerId, "❌ Задача не найдена: " + e.getMessage());
        } catch (UserNotFoundException e) {
            sendMessage(messengerId, "❌ Пользователь не найден: " + e.getMessage());
        } catch (SecurityException e) {
            sendMessage(messengerId, "⛔ Недостаточно прав: " + e.getMessage());
        } catch (Exception e) {
            sendMessage(messengerId, "❌ Произошла ошибка при получении задач: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
        }
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
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        try {
            UserDto user = identityService.findOrCreateUser(messengerId, userName);
            String messageText = message.getBody().getText();

            String[] parts = messageText.trim().split("\\s+");
            if (parts.length < 3) {
                sendMessage(messengerId, "Неверный формат команды. Используйте: /delegate {taskId} {targetUserId}");
                return;
            }

            Long taskId = Long.parseLong(parts[1]);
            Long targetUserId = Long.parseLong(parts[2]);

            taskService.delegateTask(taskId, targetUserId, user.id());
        } catch (NumberFormatException e) {
            sendMessage(messengerId, "❌ Ошибка: ID задачи и ID пользователя должны быть числами");
        } catch (TaskNotFoundException e) {
            sendMessage(messengerId, "❌ Задача не найдена: " + e.getMessage());
        } catch (UserNotFoundException e) {
            sendMessage(messengerId, "❌ Пользователь не найден: " + e.getMessage());
        } catch (SecurityException e) {
            sendMessage(messengerId, "⛔ Недостаточно прав: " + e.getMessage());
        } catch (Exception e) {
            sendMessage(messengerId, "❌ Произошла ошибка при делегировании задачи: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
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
        Long messengerId = message.getFrom().getUserId();
        String userName = message.getFrom().getUsername();

        try {
            UserDto user = identityService.findOrCreateUser(messengerId, userName);
            String messageText = message.getBody().getText();

            String[] parts = messageText.trim().split("\\s+");
            if (parts.length < 2) {
                sendMessage(messengerId, "Неверный формат команды. Используйте: /complete {taskId}");
                return;
            }

            Long taskId = Long.parseLong(parts[1]);

            taskService.completeTask(taskId, user.id());
        } catch (NumberFormatException e) {
            sendMessage(messengerId, "❌ Ошибка: ID задачи должен быть числом");
        } catch (TaskNotFoundException e) {
            sendMessage(messengerId, "❌ Задача не найдена: " + e.getMessage());
        } catch (UserNotFoundException e) {
            sendMessage(messengerId, "❌ Пользователь не найден: " + e.getMessage());
        } catch (SecurityException e) {
            sendMessage(messengerId, "⛔ Недостаточно прав: " + e.getMessage());
        } catch (Exception e) {
            sendMessage(messengerId, "❌ Произошла ошибка при завершении задачи: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
        }
    }

    /**
     * Извлекает название задачи из текста сообщения.
     *
     * @param messageText текст сообщения с командой
     * @return название задачи
     */
    private String extractTaskTitle(String messageText) {
        if (messageText != null && messageText.startsWith("/addTask ")) {
            return messageText.substring("/addTask ".length()).trim();
        }
        return messageText != null ? messageText.trim() : "";
    }

    // Вспомогательный метод для отправки сообщений пользователю
    private void sendMessage(Long messengerId, String text) {
        new SendMessageQuery(maxClient, new NewMessageBody(text))
            .userId(messengerId)
            .execute();
    }

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
     * @param requesterId внутренний ID пользователя, выполняющего обновление
     * @return обновлённая задача
     */
    @PutMapping("/tasks/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto, @RequestParam Long requesterId) {
        return taskService.updateTask(id, taskDto, requesterId);
    }

    /**
     * Удаляет задачу.
     *
     * @param id идентификатор задачи
     * @param requesterId внутренний ID пользователя, выполняющего удаление
     */
    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id, @RequestParam Long requesterId) {
        taskService.deleteTask(id, requesterId);
    }
}

