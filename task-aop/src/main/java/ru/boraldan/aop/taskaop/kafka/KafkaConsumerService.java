package ru.boraldan.aop.taskaop.kafka;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.service.EmailService;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final EmailService emailService;

    @KafkaListener(
            topics = "${kafka.consumer.tasks-update-status.topic}",
            groupId = "${kafka.consumer.tasks-update-status.group-id}",
            containerFactory = "tasksDtoListenerContainerFactory",
            properties = {"auto.offset.reset=${kafka.consumer.tasks-update-status.auto-offset-reset}"})
    public void listenTasksStatus(@Payload TasksDto tasksDto, Acknowledgment ack) {
        CompletableFuture<Void> future = emailService.sendSimpleMessageAsync(tasksDto);
        future.whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Ошибка при отправке письма: " + ex.getMessage());
            } else {
                ack.acknowledge();
            }
        });
    }

}
