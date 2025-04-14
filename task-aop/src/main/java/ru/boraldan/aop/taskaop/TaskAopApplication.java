package ru.boraldan.aop.taskaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EntityScan(basePackages = "task.entity")
@EnableJpaRepositories(basePackages = "ru.boraldan.aop.taskaop.repository")
@Import({ru.boraldan.taskstarters.logaopstarter.config.LoggerAopAutoConfiguration.class,
        ru.boraldan.taskstarters.kafkastarter.config.KafkaAutoConfig.class})
public class TaskAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskAopApplication.class, args);
    }

}
