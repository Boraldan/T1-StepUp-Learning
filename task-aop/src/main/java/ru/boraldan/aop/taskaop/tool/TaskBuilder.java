package ru.boraldan.aop.taskaop.tool;

import ru.boraldan.aop.taskaop.domen.Tasks;

import java.time.LocalDateTime;

public class TaskBuilder {

    private final Tasks tasks;

    private TaskBuilder() {
        this.tasks = new Tasks();
    }

    public static TaskBuilder create() {
        return new TaskBuilder();
    }

    public TaskBuilder setTaskId(Long taskId) {
        this.tasks.setTasksId(taskId);
        return this;
    }

    public TaskBuilder setTitle(String title) {
        this.tasks.setTitle(title);
        return this;
    }

    public TaskBuilder setDescription(String description) {
        this.tasks.setDescription(description);
        return this;
    }

    public TaskBuilder setUserId(Long userId) {
        this.tasks.setUserId(userId);
        return this;
    }

    public TaskBuilder setCreatedAt(LocalDateTime createdAt) {
        this.tasks.setCreatedAt(createdAt);
        return this;
    }

    public TaskBuilder setUpdatedAt(LocalDateTime updatedAt) {
        this.tasks.setUpdatedAt(updatedAt);
        return this;
    }

    public TaskBuilder setIsActive(Boolean isActive) {
        this.tasks.setIsActive(isActive);
        return this;
    }

    public Tasks build() {
        return this.tasks;
    }
}