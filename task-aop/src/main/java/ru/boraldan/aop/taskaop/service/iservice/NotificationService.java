package ru.boraldan.aop.taskaop.service.iservice;

import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {

     CompletableFuture<Void> sendSimpleMessageAsync(TasksDto tasksDto);
}
