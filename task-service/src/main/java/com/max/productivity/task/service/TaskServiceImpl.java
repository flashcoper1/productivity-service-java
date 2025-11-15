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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final IdentityService identityService;

    public TaskServiceImpl(TaskRepository taskRepository,
                          ApplicationEventPublisher eventPublisher,
                          IdentityService identityService) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.identityService = identityService;
    }

    @Override
    public TaskDto createTask(CreateTaskRequest request) {
        Task task = Task.builder()
            .title(request.title())
            .status("TODO")
            .priority(request.priority())
            .ownerId(request.creatorUserId())
            .dueDate(request.dueDate())
            .build();

        Task savedTask = taskRepository.save(task);

        // Публикуем событие создания задачи
        TaskCreatedEvent event = new TaskCreatedEvent(this, savedTask.getId());
        eventPublisher.publishEvent(event);

        return mapToDto(savedTask);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return mapToDto(task);
    }

    @Override
    public List<TaskDto> getTasksByUserId(Long userId) {
        return taskRepository.findByOwnerId(userId).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksForUser(Long userId) {
        return taskRepository.findByOwnerId(userId).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto, Long requesterId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        if (!task.getOwnerId().equals(requesterId)) {
            throw new SecurityException("User " + requesterId + " does not have permission to modify task " + id);
        }

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setStatus(taskDto.status());
        task.setPriority(taskDto.priority());
        task.setDueDate(taskDto.dueDate());

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id, Long requesterId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        if (!task.getOwnerId().equals(requesterId)) {
            throw new SecurityException("User " + requesterId + " does not have permission to modify task " + id);
        }

        taskRepository.deleteById(id);
    }

    @Override
    public void delegateTask(Long taskId, Long targetUserId, Long requesterId) {
        // 1. Найти задачу или выбросить TaskNotFoundException
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (!task.getOwnerId().equals(requesterId)) {
            throw new SecurityException("User " + requesterId + " does not have permission to modify task " + taskId);
        }

        // 2. Проверить существование пользователя по внутреннему ID через identityService
        if (!identityService.userExistsById(targetUserId)) {
            throw new UserNotFoundException(targetUserId);
        }

        // 3. Сохранить предыдущего владельца для события
        Long previousOwnerId = task.getOwnerId();

        // 4. Изменить ownerId у задачи
        task.setOwnerId(targetUserId);

        // 5. Сохранить задачу
        taskRepository.save(task);

        // 6. Опубликовать TaskDelegatedEvent
        TaskDelegatedEvent event = new TaskDelegatedEvent(
            this,
            taskId,
            previousOwnerId,
            targetUserId
        );
        eventPublisher.publishEvent(event);
    }

    @Override
    public void completeTask(Long taskId, Long requesterId) {
        // 1. Найти задачу или выбросить TaskNotFoundException
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        if (!task.getOwnerId().equals(requesterId)) {
            throw new SecurityException("User " + requesterId + " does not have permission to modify task " + taskId);
        }

        // 2. Установить статус задачи в 'COMPLETED'
        task.setStatus("COMPLETED");

        // 3. Сохранить задачу
        taskRepository.save(task);

        // 4. Опубликовать TaskCompletedEvent
        TaskCompletedEvent event = new TaskCompletedEvent(this, taskId);
        eventPublisher.publishEvent(event);
    }

    private TaskDto mapToDto(Task task) {
        return new TaskDto(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            task.getOwnerId()
        );
    }
}

