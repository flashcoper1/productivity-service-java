package com.max.productivity.identity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA-сущность пользователя.
 * Представляет пользователя системы управления задачами.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя в системе.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя из мессенджера.
     * Уникальный для каждого пользователя, не может быть null.
     */
    @Column(nullable = false, unique = true)
    private Long messengerId;

    /**
     * Имя пользователя.
     * Не может быть null.
     */
    @Column(nullable = false)
    private String userName;

    /**
     * Дата и время регистрации пользователя в системе.
     */
    private Instant registeredAt;
}

        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

