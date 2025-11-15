package com.max.productivity.identity.domain;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA-сущность пользователя.
 * Представляет пользователя системы управления задачами.
 */
@Entity
@Table(name = "users")
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

    // Constructors
    public User() {
    }

    public User(Long id, Long messengerId, String userName, Instant registeredAt) {
        this.id = id;
        this.messengerId = messengerId;
        this.userName = userName;
        this.registeredAt = registeredAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessengerId() {
        return messengerId;
    }

    public void setMessengerId(Long messengerId) {
        this.messengerId = messengerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long messengerId;
        private String userName;
        private Instant registeredAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder messengerId(Long messengerId) {
            this.messengerId = messengerId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder registeredAt(Instant registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public User build() {
            return new User(id, messengerId, userName, registeredAt);
        }
    }
}

