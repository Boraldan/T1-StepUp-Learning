package ru.boraldan.aop.taskaop.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TaskTestContainers {

    @Container
    protected static final KafkaContainer KAFKA_CONTAINER =
//            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
            new KafkaContainer(DockerImageName.parse("apache/kafka:latest"));

    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

}
