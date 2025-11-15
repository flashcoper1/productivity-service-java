package com.max.productivity.task.repo;

import com.max.productivity.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с задачами (Task).
 * Расширяет JpaRepository для предоставления стандартных CRUD операций.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Находит все задачи, принадлежащие указанному владельцу.
     *
     * @param ownerId идентификатор владельца задач
     * @return список задач владельца
     */
    List<Task> findByOwnerId(Long ownerId);

    /**
     * Находит все задачи владельца с указанным статусом.
     *
     * @param ownerId идентификатор владельца задач
     * @param status статус задачи (например, "TODO", "IN_PROGRESS", "DONE")
     * @return список задач владельца с указанным статусом
     */
    List<Task> findByOwnerIdAndStatus(Long ownerId, String status);
}

