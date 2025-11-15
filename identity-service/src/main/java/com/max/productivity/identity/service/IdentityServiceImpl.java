package com.max.productivity.identity.service;

import com.max.productivity.common.dto.UserDto;
import com.max.productivity.identity.domain.User;
import com.max.productivity.identity.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Реализация сервиса для управления пользователями.
 */
@Service
@Transactional
public class IdentityServiceImpl implements IdentityService {

    private final UserRepository userRepository;

    public IdentityServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean userExists(Long messengerId) {
        return userRepository.findByMessengerId(messengerId).isPresent();
    }

    @Override
    public boolean userExistsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserDto findOrCreateUser(Long messengerId, String userName) {
        // Ищем существующего пользователя
        Optional<User> existingUser = userRepository.findByMessengerId(messengerId);

        if (existingUser.isPresent()) {
            // Пользователь найден - возвращаем его DTO
            return mapToDto(existingUser.get());
        }

        // Пользователь не найден - создаём нового
        User newUser = User.builder()
            .messengerId(messengerId)
            .userName(userName)
            .registeredAt(Instant.now())
            .build();

        User savedUser = userRepository.save(newUser);

        return mapToDto(savedUser);
    }

    @Override
    public Optional<UserDto> findUserByMessengerId(Long messengerId) {
        return userRepository.findByMessengerId(messengerId)
            .map(this::mapToDto);
    }

    @Override
    public Optional<UserDto> findUserById(Long userId) {
        return userRepository.findById(userId)
            .map(this::mapToDto);
    }

    /**
     * Преобразует сущность User в DTO.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    private UserDto mapToDto(User user) {
        return new UserDto(
            user.getId(),
            user.getMessengerId(),
            user.getUserName()
        );
    }
}

