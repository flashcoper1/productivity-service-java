package com.max.productivity.identity.service;

import com.max.productivity.common.dto.UserDto;

import java.util.Optional;

/**
 * Сервис для управления пользователями в системе.
 * Предоставляет методы для работы с информацией о пользователях из мессенджера.
 */
public interface IdentityService {

    /**
     * Проверяет существование пользователя с заданным ID мессенджера в системе.
     *
     * @param messengerId идентификатор пользователя в мессенджере для проверки
     * @return true, если пользователь с указанным messengerId существует, иначе false
     */
    boolean userExists(Long messengerId);

    /**
     * Проверяет существование пользователя с заданным внутренним ID в системе.
     *
     * @param userId внутренний идентификатор пользователя для проверки
     * @return true, если пользователь с указанным ID существует, иначе false
     */
    boolean userExistsById(Long userId);

    /**
     * Находит пользователя по идентификатору мессенджера или создаёт нового, если не найден.
     * Гарантирует возврат DTO пользователя в любом случае.
     *
     * @param messengerId идентификатор пользователя в мессенджере
     * @param userName имя пользователя для создания нового профиля
     * @return DTO пользователя (существующего или вновь созданного)
     */
    UserDto findOrCreateUser(Long messengerId, String userName);

    /**
     * Находит пользователя по идентификатору мессенджера.
     *
     * @param messengerId идентификатор пользователя в мессенджере
     * @return Optional с DTO пользователя, если найден, иначе пустой Optional
     */
    Optional<UserDto> findUserByMessengerId(Long messengerId);
}

