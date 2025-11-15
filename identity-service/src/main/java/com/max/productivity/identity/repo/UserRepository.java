package com.max.productivity.identity.repo;

import com.max.productivity.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User.
 * Предоставляет методы для доступа к данным пользователей.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по идентификатору из мессенджера.
     *
     * @param messengerId идентификатор пользователя в мессенджере
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByMessengerId(Long messengerId);
}

