package ru.boraldan.aop.taskaop.kafka;

import lombok.RequiredArgsConstructor;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.boraldan.aop.taskaop.service.NotificationService;
import task.dto.TasksDto;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "kafka.enable", havingValue = "true")
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.consumer.tasks-update-status.topic}",
            groupId = "${kafka.consumer.tasks-update-status.group-id}",
            containerFactory = "${kafka.consumer.tasks-update-status.container-factory}",
            properties = {"auto.offset.reset=${kafka.consumer.tasks-update-status.auto-offset-reset}"})
    public void listenTasksStatus(@Payload TasksDto tasksDto, Acknowledgment ack) {
        CompletableFuture<Void> future = notificationService.sendSimpleMessageAsync(tasksDto);
        future.whenComplete((res, ex) -> {
            if (ex != null) {
                System.err.println("Ошибка при отправке письма: " + ex.getMessage());
            } else {
                ack.acknowledge();
            }
        });
    }

}
