package ru.boraldan.taskstarters.kafkastarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka.consumer.tasks-update-status")
public class KafkaConsumerProperties {

    private String topic;
    private String groupId;
    private String containerFactory;
    private String autoOffsetReset;
    private String payloadDto;
}
