package ru.boraldan.aop.taskaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@ImportAutoConfiguration({
        ru.boraldan.logaopstarter.starter.config.LoggerAopAutoConfiguration.class,
        ru.boraldan.taskstarters.kafkastarter.config.KafkaAutoConfig.class
})
public class TaskAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskAopApplication.class, args);
    }

}
