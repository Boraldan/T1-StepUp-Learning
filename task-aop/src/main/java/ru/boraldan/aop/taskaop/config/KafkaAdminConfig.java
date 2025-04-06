package ru.boraldan.aop.taskaop.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.tasks-update-status.topic}")
    private String topic;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(config);
    }

    // Создает новый Kafka топик с именем "tasks-update-status-topic" с 2 партициями и 1 репликой.
    @Bean
    public NewTopic tasksTopic() {
        return new NewTopic(topic, 2, (short) 1);
    }
}
