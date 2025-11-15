package com.max.productivity.task.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status = "TODO";

    private int priority = 0;

    private Instant createdAt;

    private Instant dueDate;

    @Column(nullable = false)
    private Long ownerId;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = "TODO";
        }
    }

    // Constructors
    public Task() {
    }

    public Task(Long id, String title, String description, String status, int priority, Instant createdAt, Instant dueDate, Long ownerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : "TODO";
        this.priority = priority;
        this.createdAt = createdAt;
        this.dueDate = dueDate;
        this.ownerId = ownerId;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getPriority() {
        return priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String status = "TODO";
        private int priority = 0;
        private Instant createdAt;
        private Instant dueDate;
        private Long ownerId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder dueDate(Instant dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Task build() {
            return new Task(id, title, description, status, priority, createdAt, dueDate, ownerId);
        }
    }
}
