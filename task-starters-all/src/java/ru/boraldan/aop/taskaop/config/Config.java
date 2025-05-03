package ru.boraldan.aop.taskaop.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Bean
    @ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true")
    public NewTopic taskStatusUpdateTopic() {
        return new NewTopic("tasks-update-status-topic", 1, (short) 1);
    }
}
