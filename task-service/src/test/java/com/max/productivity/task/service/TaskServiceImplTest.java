package com.max.productivity.task.service;

import com.max.productivity.common.dto.TaskDto;
import com.max.productivity.common.exception.TaskNotFoundException;
import com.max.productivity.identity.exception.UserNotFoundException;
import com.max.productivity.identity.service.IdentityService;
import com.max.productivity.task.domain.Task;
import com.max.productivity.task.dto.CreateTaskRequest;
import com.max.productivity.task.event.TaskCompletedEvent;
import com.max.productivity.task.event.TaskCreatedEvent;
import com.max.productivity.task.event.TaskDelegatedEvent;
import com.max.productivity.task.repo.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для TaskServiceImpl.
 * Проверяет основную бизнес-логику сервиса управления задачами.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private IdentityService identityService;

    @InjectMocks
    private TaskServiceImpl taskService;

    /**
     * Тест создания задачи: проверяет сохранение в репозиторий и публикацию события.
     */
    @Test
    void whenCreateTask_thenSaveAndPublishEvent_shouldSucceed() {
        // Arrange - подготовка тестовых данных
        String taskTitle = "Изучить Spring Boot";
        int priority = 1;
        Instant dueDate = Instant.parse("2025-11-20T15:00:00Z");
        Long creatorUserId = 123L;

        CreateTaskRequest request = new CreateTaskRequest(
            taskTitle,
            priority,
            dueDate,
            creatorUserId
        );

        // Создаем сущность Task, которая будет возвращена из репозитория
        Task savedTask = Task.builder()
            .id(1L)
            .title(taskTitle)
            .description(null)
            .status("TODO")
            .priority(priority)
            .ownerId(creatorUserId)
            .dueDate(dueDate)
            .createdAt(Instant.now())
            .build();

        // Настройка мока: taskRepository.save() возвращает сохраненную сущность
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act - выполнение тестируемого метода
        TaskDto result = taskService.createTask(request);

        // Assert - проверка результатов

        // 1. Проверяем, что taskRepository.save() был вызван один раз
        verify(taskRepository, times(1)).save(any(Task.class));

        // 2. Проверяем, что eventPublisher.publishEvent() был вызван один раз с TaskCreatedEvent
        ArgumentCaptor<TaskCreatedEvent> eventCaptor = ArgumentCaptor.forClass(TaskCreatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        // 3. Проверяем, что событие содержит правильный taskId
        TaskCreatedEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(1L, publishedEvent.getTaskId());

        // 4. Проверяем, что возвращенный DTO не null
        assertNotNull(result);

        // 5. Проверяем, что поля DTO соответствуют входным данным
        assertEquals(1L, result.id());
        assertEquals(taskTitle, result.title());
        assertEquals("TODO", result.status());
        assertEquals(priority, result.priority());
        assertEquals(dueDate, result.dueDate());
        assertEquals(creatorUserId, result.assignedToUserId());
    }

    /**
     * Тест получения задачи по ID: проверяет успешное получение существующей задачи.
     */
    @Test
    void whenGetTaskById_thenReturnTask_shouldSucceed() {
        // Arrange
        Long taskId = 1L;
        Task task = Task.builder()
            .id(taskId)
            .title("Тестовая задача")
            .description("Описание задачи")
            .status("TODO")
            .priority(1)
            .ownerId(123L)
            .dueDate(Instant.parse("2025-11-20T15:00:00Z"))
            .createdAt(Instant.now())
            .build();

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        // Act
        TaskDto result = taskService.getTaskById(taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.id());
        assertEquals("Тестовая задача", result.title());
        assertEquals("Описание задачи", result.description());
        verify(taskRepository, times(1)).findById(taskId);
    }

    /**
     * Тест получения всех задач пользователя: проверяет возврат списка задач.
     */
    @Test
    void whenGetTasksByUserId_thenReturnTaskList_shouldSucceed() {
        // Arrange
        Long userId = 123L;
        Task task1 = Task.builder()
            .id(1L)
            .title("Задача 1")
            .status("TODO")
            .priority(1)
            .ownerId(userId)
            .build();

        Task task2 = Task.builder()
            .id(2L)
            .title("Задача 2")
            .status("IN_PROGRESS")
            .priority(2)
            .ownerId(userId)
            .build();

        when(taskRepository.findByOwnerId(userId))
            .thenReturn(java.util.List.of(task1, task2));

        // Act
        var result = taskService.getTasksByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Задача 1", result.get(0).title());
        assertEquals("Задача 2", result.get(1).title());
        verify(taskRepository, times(1)).findByOwnerId(userId);
    }

    /**
     * Тест получения задач для пользователя через метод getTasksForUser.
     * Проверяет, что список задач корректно преобразуется в список DTO.
     */
    @Test
    void whenUserHasTasks_getTasksForUser_shouldReturnTaskDtoList() {
        // Arrange - создаем список из 3 мок-сущностей Task
        Long userId = 456L;

        Task task1 = Task.builder()
            .id(10L)
            .title("Изучить JUnit 5")
            .description("Написать unit-тесты")
            .status("TODO")
            .priority(2)
            .ownerId(userId)
            .dueDate(Instant.parse("2025-11-20T10:00:00Z"))
            .createdAt(Instant.parse("2025-11-15T08:00:00Z"))
            .build();

        Task task2 = Task.builder()
            .id(11L)
            .title("Изучить Mockito")
            .description("Понять моки и стабы")
            .status("IN_PROGRESS")
            .priority(1)
            .ownerId(userId)
            .dueDate(Instant.parse("2025-11-21T12:00:00Z"))
            .createdAt(Instant.parse("2025-11-15T09:00:00Z"))
            .build();

        Task task3 = Task.builder()
            .id(12L)
            .title("Написать интеграционные тесты")
            .description("Покрыть REST API тестами")
            .status("TODO")
            .priority(3)
            .ownerId(userId)
            .dueDate(Instant.parse("2025-11-22T14:00:00Z"))
            .createdAt(Instant.parse("2025-11-15T10:00:00Z"))
            .build();

        // Настраиваем мок taskRepository.findByOwnerId() для возврата списка задач
        when(taskRepository.findByOwnerId(userId))
            .thenReturn(java.util.List.of(task1, task2, task3));

        // Act - вызываем тестируемый метод
        java.util.List<TaskDto> result = taskService.getTasksForUser(userId);

        // Assert - проверяем результаты

        // 1. Проверяем, что возвращенный список не null
        assertNotNull(result, "Список задач не должен быть null");

        // 2. Проверяем, что размер списка DTO соответствует размеру списка мок-сущностей
        assertEquals(3, result.size(), "Размер списка должен быть 3");

        // 3. Проверяем, что данные в первом DTO соответствуют данным первой мок-сущности
        TaskDto firstDto = result.get(0);
        assertEquals(10L, firstDto.id(), "ID первой задачи должен быть 10");
        assertEquals("Изучить JUnit 5", firstDto.title(), "Название первой задачи должно совпадать");
        assertEquals("Написать unit-тесты", firstDto.description(), "Описание первой задачи должно совпадать");
        assertEquals("TODO", firstDto.status(), "Статус первой задачи должен быть TODO");
        assertEquals(2, firstDto.priority(), "Приоритет первой задачи должен быть 2");
        assertEquals(Instant.parse("2025-11-20T10:00:00Z"), firstDto.dueDate(), "Срок первой задачи должен совпадать");
        assertEquals(userId, firstDto.assignedToUserId(), "ID пользователя первой задачи должен совпадать");

        // 4. Проверяем данные во втором DTO
        TaskDto secondDto = result.get(1);
        assertEquals(11L, secondDto.id(), "ID второй задачи должен быть 11");
        assertEquals("Изучить Mockito", secondDto.title(), "Название второй задачи должно совпадать");
        assertEquals("IN_PROGRESS", secondDto.status(), "Статус второй задачи должен быть IN_PROGRESS");
        assertEquals(1, secondDto.priority(), "Приоритет второй задачи должен быть 1");

        // 5. Проверяем данные в третьем DTO
        TaskDto thirdDto = result.get(2);
        assertEquals(12L, thirdDto.id(), "ID третьей задачи должен быть 12");
        assertEquals("Написать интеграционные тесты", thirdDto.title(), "Название третьей задачи должно совпадать");
        assertEquals(3, thirdDto.priority(), "Приоритет третьей задачи должен быть 3");

        // 6. Проверяем, что репозиторий был вызван ровно один раз с правильным userId
        verify(taskRepository, times(1)).findByOwnerId(userId);
    }

    /**
     * Тест удаления задачи: проверяет успешное удаление существующей задачи.
     */
    @Test
    void whenDeleteTask_thenRemoveFromRepository_shouldSucceed() {
        // Arrange
        Long taskId = 1L;
        Long requesterId = 123L;

        Task task = Task.builder()
            .id(taskId)
            .ownerId(requesterId)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act
        taskService.deleteTask(taskId, requesterId);

        // Assert
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    /**
     * Тест обновления задачи: проверяет успешное обновление полей задачи.
     */
    @Test
    void whenUpdateTask_thenSaveUpdatedTask_shouldSucceed() {
        // Arrange
        Long taskId = 1L;
        Long requesterId = 123L;
        Task existingTask = Task.builder()
            .id(taskId)
            .title("Старый заголовок")
            .description("Старое описание")
            .status("TODO")
            .priority(1)
            .ownerId(requesterId)
            .build();

        TaskDto updateDto = new TaskDto(
            taskId,
            "Новый заголовок",
            "Новое описание",
            "IN_PROGRESS",
            2,
            Instant.parse("2025-11-25T15:00:00Z"),
            requesterId
        );

        Task updatedTask = Task.builder()
            .id(taskId)
            .title("Новый заголовок")
            .description("Новое описание")
            .status("IN_PROGRESS")
            .priority(2)
            .ownerId(requesterId)
            .dueDate(Instant.parse("2025-11-25T15:00:00Z"))
            .build();

        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskDto result = taskService.updateTask(taskId, updateDto, requesterId);

        // Assert
        assertNotNull(result);
        assertEquals("Новый заголовок", result.title());
        assertEquals("Новое описание", result.description());
        assertEquals("IN_PROGRESS", result.status());
        assertEquals(2, result.priority());
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Тест делегирования задачи: успешный сценарий.
     * Проверяет, что задача делегируется, пользователь проверяется, и событие публикуется.
     */
    @Test
    void whenDelegateTask_withValidData_shouldSucceed() {
        // Arrange
        Long taskId = 1L;
        Long previousOwnerId = 50L;
        Long newOwnerId = 100L;
        Long requesterId = previousOwnerId; // Запросчик - текущий владелец

        Task task = Task.builder()
            .id(taskId)
            .title("Тестовая задача")
            .ownerId(previousOwnerId)
            .status("TODO")
            .priority(1)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(identityService.userExistsById(newOwnerId)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.delegateTask(taskId, newOwnerId, requesterId);

        // Assert
        // 1. Проверяем, что владелец задачи изменился
        assertEquals(newOwnerId, task.getOwnerId());

        // 2. Проверяем, что identityService.userExistsById был вызван
        verify(identityService, times(1)).userExistsById(newOwnerId);

        // 3. Проверяем, что задача была сохранена
        verify(taskRepository, times(1)).save(task);

        // 4. Проверяем, что событие TaskDelegatedEvent было опубликовано
        ArgumentCaptor<TaskDelegatedEvent> eventCaptor = ArgumentCaptor.forClass(TaskDelegatedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        TaskDelegatedEvent event = eventCaptor.getValue();
        assertEquals(taskId, event.getTaskId());
        assertEquals(previousOwnerId, event.getPreviousOwnerId());
        assertEquals(newOwnerId, event.getNewOwnerId());
    }

    /**
     * Тест делегирования задачи: задача не найдена.
     * Проверяет выброс TaskNotFoundException.
     */
    @Test
    void whenDelegateTask_taskNotFound_shouldThrowTaskNotFoundException() {
        // Arrange
        Long taskId = 999L;
        Long targetUserId = 100L;
        Long requesterId = 50L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class,
            () -> taskService.delegateTask(taskId, targetUserId, requesterId),
            "Должно быть выброшено TaskNotFoundException когда задача не найдена");

        // Проверяем, что identityService не был вызван
        verify(identityService, never()).userExistsById(any());
        // Проверяем, что событие не было опубликовано
        verify(eventPublisher, never()).publishEvent(any());
    }

    /**
     * Тест делегирования задачи: целевой пользователь не найден.
     * Проверяет выброс UserNotFoundException.
     */
    @Test
    void whenDelegateTask_userNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        Long taskId = 1L;
        Long targetUserId = 999L;
        Long requesterId = 50L;

        Task task = Task.builder()
            .id(taskId)
            .ownerId(requesterId)
            .title("Тестовая задача")
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(identityService.userExistsById(targetUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> taskService.delegateTask(taskId, targetUserId, requesterId),
            "Должно быть выброшено UserNotFoundException когда пользователь не найден");

        // Проверяем, что identityService был вызван
        verify(identityService, times(1)).userExistsById(targetUserId);
        // Проверяем, что save не был вызван
        verify(taskRepository, never()).save(any());
        // Проверяем, что событие НЕ было опубликовано
        verify(eventPublisher, never()).publishEvent(any());
    }

    /**
     * Тест завершения задачи: успешный сценарий.
     * Проверяет, что статус задачи изменяется на COMPLETED и публикуется событие.
     */
    @Test
    void whenCompleteTask_withValidId_shouldSucceed() {
        // Arrange
        Long taskId = 1L;
        Long requesterId = 123L;
        Task task = Task.builder()
            .id(taskId)
            .title("Задача для завершения")
            .status("TODO")
            .ownerId(requesterId)
            .priority(1)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.completeTask(taskId, requesterId);

        // Assert
        // 1. Проверяем, что статус изменился на COMPLETED
        assertEquals("COMPLETED", task.getStatus());

        // 2. Проверяем, что задача была сохранена
        verify(taskRepository, times(1)).save(task);

        // 3. Проверяем, что событие TaskCompletedEvent было опубликовано
        ArgumentCaptor<TaskCompletedEvent> eventCaptor = ArgumentCaptor.forClass(TaskCompletedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        TaskCompletedEvent event = eventCaptor.getValue();
        assertEquals(taskId, event.getTaskId());
    }

    /**
     * Тест завершения задачи: задача не найдена.
     * Проверяет выброс TaskNotFoundException.
     */
    @Test
    void whenCompleteTask_taskNotFound_shouldThrowTaskNotFoundException() {
        // Arrange
        Long taskId = 999L;
        Long requesterId = 123L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class,
            () -> taskService.completeTask(taskId, requesterId),
            "Должно быть выброшено TaskNotFoundException когда задача не найдена");

        // Проверяем, что событие не было опубликовано
        verify(eventPublisher, never()).publishEvent(any());
        // Проверяем, что save не был вызван
        verify(taskRepository, never()).save(any());
    }

    /**
     * Тест завершения задачи не владельцем: проверка безопасности.
     * Проверяет, что SecurityException выбрасывается, если requesterId не совпадает с ownerId.
     */
    @Test
    void whenCompleteTask_byNonOwner_shouldThrowSecurityException() {
        // Arrange
        Long taskId = 1L;
        Long ownerId = 123L;
        Long requesterId = 999L; // Другой пользователь, не владелец

        Task task = Task.builder()
            .id(taskId)
            .title("Задача другого пользователя")
            .status("TODO")
            .ownerId(ownerId)
            .priority(1)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> taskService.completeTask(taskId, requesterId),
            "Должно быть выброшено SecurityException когда пользователь не является владельцем задачи");

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("does not have permission to modify task"));
        assertTrue(exception.getMessage().contains(requesterId.toString()));
        assertTrue(exception.getMessage().contains(taskId.toString()));

        // Проверяем, что задача НЕ была сохранена
        verify(taskRepository, never()).save(any());
        // Проверяем, что событие НЕ было опубликовано
        verify(eventPublisher, never()).publishEvent(any());
    }

    /**
     * Тест делегирования задачи не владельцем: проверка безопасности.
     * Проверяет, что SecurityException выбрасывается, если requesterId не совпадает с ownerId.
     */
    @Test
    void whenDelegateTask_byNonOwner_shouldThrowSecurityException() {
        // Arrange
        Long taskId = 1L;
        Long ownerId = 50L;
        Long requesterId = 999L; // Другой пользователь, не владелец
        Long targetUserId = 100L;

        Task task = Task.builder()
            .id(taskId)
            .title("Задача другого пользователя")
            .ownerId(ownerId)
            .status("TODO")
            .priority(1)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> taskService.delegateTask(taskId, targetUserId, requesterId),
            "Должно быть выброшено SecurityException когда пользователь не является владельцем задачи");

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("does not have permission to modify task"));
        assertTrue(exception.getMessage().contains(requesterId.toString()));
        assertTrue(exception.getMessage().contains(taskId.toString()));

        // Проверяем, что identityService НЕ был вызван (проверка должна произойти до этого)
        verify(identityService, never()).userExistsById(any());
        // Проверяем, что задача НЕ была сохранена
        verify(taskRepository, never()).save(any());
        // Проверяем, что событие НЕ было опубликовано
        verify(eventPublisher, never()).publishEvent(any());
    }

    /**
     * Тест обновления задачи не владельцем: проверка безопасности.
     * Проверяет, что SecurityException выбрасывается, если requesterId не совпадает с ownerId.
     */
    @Test
    void whenUpdateTask_byNonOwner_shouldThrowSecurityException() {
        // Arrange
        Long taskId = 1L;
        Long ownerId = 123L;
        Long requesterId = 999L; // Другой пользователь, не владелец

        Task existingTask = Task.builder()
            .id(taskId)
            .title("Задача другого пользователя")
            .description("Описание")
            .status("TODO")
            .priority(1)
            .ownerId(ownerId)
            .build();

        TaskDto updateDto = new TaskDto(
            taskId,
            "Обновленный заголовок",
            "Обновленное описание",
            "IN_PROGRESS",
            2,
            Instant.parse("2025-11-25T15:00:00Z"),
            ownerId
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> taskService.updateTask(taskId, updateDto, requesterId),
            "Должно быть выброшено SecurityException когда пользователь не является владельцем задачи");

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("does not have permission to modify task"));
        assertTrue(exception.getMessage().contains(requesterId.toString()));
        assertTrue(exception.getMessage().contains(taskId.toString()));

        // Проверяем, что задача НЕ была сохранена
        verify(taskRepository, never()).save(any());
    }

    /**
     * Тест удаления задачи не владельцем: проверка безопасности.
     * Проверяет, что SecurityException выбрасывается, если requesterId не совпадает с ownerId.
     */
    @Test
    void whenDeleteTask_byNonOwner_shouldThrowSecurityException() {
        // Arrange
        Long taskId = 1L;
        Long ownerId = 123L;
        Long requesterId = 999L; // Другой пользователь, не владелец

        Task task = Task.builder()
            .id(taskId)
            .title("Задача другого пользователя")
            .ownerId(ownerId)
            .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class,
            () -> taskService.deleteTask(taskId, requesterId),
            "Должно быть выброшено SecurityException когда пользователь не является владельцем задачи");

        // Проверяем сообщение об ошибке
        assertTrue(exception.getMessage().contains("does not have permission to modify task"));
        assertTrue(exception.getMessage().contains(requesterId.toString()));
        assertTrue(exception.getMessage().contains(taskId.toString()));

        // Проверяем, что задача НЕ была удалена
        verify(taskRepository, never()).deleteById(any());
    }
}

