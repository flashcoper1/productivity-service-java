package com.max.productivity.identity.service;

import com.max.productivity.common.dto.UserDto;
import com.max.productivity.identity.domain.User;
import com.max.productivity.identity.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для IdentityServiceImpl.
 * Проверяет бизнес-логику сервиса управления пользователями.
 */
@ExtendWith(MockitoExtension.class)
class IdentityServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IdentityServiceImpl identityService;

    /**
     * Тест findOrCreateUser: пользователь НЕ существует.
     * Проверяет, что новый пользователь создаётся и сохраняется в БД.
     */
    @Test
    void whenFindOrCreateUser_userDoesNotExist_shouldCreateNewUser() {
        // Arrange
        Long messengerId = 123456789L;
        String userName = "Иван Иванов";

        // Мок: пользователь НЕ найден
        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.empty());

        // Мок: сохранённый пользователь
        User savedUser = User.builder()
            .id(1L)
            .messengerId(messengerId)
            .userName(userName)
            .registeredAt(Instant.now())
            .build();

        when(userRepository.save(any(User.class)))
            .thenReturn(savedUser);

        // Act
        UserDto result = identityService.findOrCreateUser(messengerId, userName);

        // Assert

        // 1. Проверяем, что findByMessengerId был вызван
        verify(userRepository, times(1)).findByMessengerId(messengerId);

        // 2. Проверяем, что save был вызван ОДИН раз (создание нового пользователя)
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        // 3. Проверяем, что сохранённый пользователь имеет правильные данные
        User capturedUser = userCaptor.getValue();
        assertEquals(messengerId, capturedUser.getMessengerId());
        assertEquals(userName, capturedUser.getUserName());
        assertNotNull(capturedUser.getRegisteredAt());

        // 4. Проверяем, что возвращаемый DTO содержит корректные данные
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(messengerId, result.messengerId());
        assertEquals(userName, result.userName());
    }

    /**
     * Тест findOrCreateUser: пользователь УЖЕ существует.
     * Проверяет, что существующий пользователь возвращается без создания нового.
     */
    @Test
    void whenFindOrCreateUser_userExists_shouldReturnExistingUser() {
        // Arrange
        Long messengerId = 987654321L;
        String userName = "Пётр Петров";

        // Существующий пользователь в БД
        User existingUser = User.builder()
            .id(5L)
            .messengerId(messengerId)
            .userName("Старое Имя")  // Имя в БД может отличаться
            .registeredAt(Instant.parse("2025-11-10T10:00:00Z"))
            .build();

        // Мок: пользователь найден
        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.of(existingUser));

        // Act
        UserDto result = identityService.findOrCreateUser(messengerId, userName);

        // Assert

        // 1. Проверяем, что findByMessengerId был вызван
        verify(userRepository, times(1)).findByMessengerId(messengerId);

        // 2. Проверяем, что save НЕ был вызван (пользователь уже существует)
        verify(userRepository, never()).save(any(User.class));

        // 3. Проверяем, что возвращаемый DTO содержит данные СУЩЕСТВУЮЩЕГО пользователя
        assertNotNull(result);
        assertEquals(5L, result.id());
        assertEquals(messengerId, result.messengerId());
        assertEquals("Старое Имя", result.userName());  // Имя из БД, не новое
    }

    /**
     * Тест findUserByMessengerId: пользователь найден.
     * Проверяет корректное преобразование User в UserDto.
     */
    @Test
    void whenFindUserByMessengerId_userExists_shouldReturnUserDto() {
        // Arrange
        Long messengerId = 111222333L;
        User user = User.builder()
            .id(10L)
            .messengerId(messengerId)
            .userName("Анна Сидорова")
            .registeredAt(Instant.parse("2025-11-12T14:30:00Z"))
            .build();

        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.of(user));

        // Act
        Optional<UserDto> result = identityService.findUserByMessengerId(messengerId);

        // Assert
        assertTrue(result.isPresent());

        UserDto userDto = result.get();
        assertEquals(10L, userDto.id());
        assertEquals(messengerId, userDto.messengerId());
        assertEquals("Анна Сидорова", userDto.userName());

        verify(userRepository, times(1)).findByMessengerId(messengerId);
    }

    /**
     * Тест findUserByMessengerId: пользователь НЕ найден.
     * Проверяет, что возвращается пустой Optional.
     */
    @Test
    void whenFindUserByMessengerId_userNotExists_shouldReturnEmptyOptional() {
        // Arrange
        Long messengerId = 999888777L;

        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.empty());

        // Act
        Optional<UserDto> result = identityService.findUserByMessengerId(messengerId);

        // Assert
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findByMessengerId(messengerId);
    }

    /**
     * Тест userExists: пользователь существует.
     */
    @Test
    void whenUserExists_userFound_shouldReturnTrue() {
        // Arrange
        Long messengerId = 555666777L;
        User user = User.builder()
            .id(3L)
            .messengerId(messengerId)
            .userName("Test User")
            .build();

        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.of(user));

        // Act
        boolean result = identityService.userExists(messengerId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByMessengerId(messengerId);
    }

    /**
     * Тест userExists: пользователь НЕ существует.
     */
    @Test
    void whenUserExists_userNotFound_shouldReturnFalse() {
        // Arrange
        Long messengerId = 444333222L;

        when(userRepository.findByMessengerId(messengerId))
            .thenReturn(Optional.empty());

        // Act
        boolean result = identityService.userExists(messengerId);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByMessengerId(messengerId);
    }

    /**
     * Тест userExistsById: пользователь существует по внутреннему ID.
     */
    @Test
    void whenUserExistsById_userFound_shouldReturnTrue() {
        // Arrange
        Long userId = 7L;

        when(userRepository.existsById(userId))
            .thenReturn(true);

        // Act
        boolean result = identityService.userExistsById(userId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    /**
     * Тест userExistsById: пользователь НЕ существует по внутреннему ID.
     */
    @Test
    void whenUserExistsById_userNotFound_shouldReturnFalse() {
        // Arrange
        Long userId = 999L;

        when(userRepository.existsById(userId))
            .thenReturn(false);

        // Act
        boolean result = identityService.userExistsById(userId);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }
}

