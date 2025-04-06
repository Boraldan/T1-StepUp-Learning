package ru.boraldan.aop.taskaop.kafka;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

@Component
public class KafkaTasksStatusProducer extends KafkaProducerFabric<TasksDto> {

    @Value("${kafka.consumer.tasks-update-status.topic}")
    private String topic;

    public KafkaTasksStatusProducer(KafkaTemplate<String, TasksDto> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendToUpdateStatus(TasksDto tasksDto) {
        super.sendTo(topic, tasksDto);
    }
}