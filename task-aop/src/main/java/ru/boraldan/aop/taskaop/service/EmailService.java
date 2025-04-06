package ru.boraldan.aop.taskaop.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.boraldan.aop.taskaop.aspect.annotation.LogAfterThrowing;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${mail.email-recipient}")
    private String emailRecipient;
    private final JavaMailSender emailSender;

    // Отправляет текстовое сообщение о смене статуса Tasks.
    @LogAfterThrowing
    @Async
    public CompletableFuture<Void> sendSimpleMessageAsync(TasksDto tasksDto) {
        String text = "Tasks id : %s \nИзменен статус : %s".formatted(tasksDto.getTasksId(), tasksDto.getStatus());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRecipient);
        message.setSubject("Изменение статуса Tasks %s".formatted(tasksDto.getTasksId()));
        message.setText(text);
        emailSender.send(message);
        return CompletableFuture.completedFuture(null);
    }



}