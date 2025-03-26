package ru.boraldan.aop.taskaop.tool;

import ru.boraldan.aop.taskaop.domen.Task;

import java.time.LocalDateTime;

public class TaskBuilder {

    private final Task task;

    private TaskBuilder() {
        this.task = new Task();
    }

    public static TaskBuilder create() {
        return new TaskBuilder();
    }

    public TaskBuilder setTaskId(Long taskId) {
        this.task.setTaskId(taskId);
        return this;
    }

    public TaskBuilder setTitle(String title) {
        this.task.setTitle(title);
        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.task.setDescription(description);
        return this;
    }

    public TaskBuilder setUserId(Long userId) {
        this.task.setUserId(userId);
        return this;
    }

    public TaskBuilder setCreatedAt(LocalDateTime createdAt) {
        this.task.setCreatedAt(createdAt);
        return this;
    }

    public TaskBuilder setUpdatedAt(LocalDateTime updatedAt) {
        this.task.setUpdatedAt(updatedAt);
        return this;
    }

    public TaskBuilder setIsActive(Boolean isActive) {
        this.task.setIsActive(isActive);
        return this;
    }

    public Task build() {
        return this.task;
    }
}