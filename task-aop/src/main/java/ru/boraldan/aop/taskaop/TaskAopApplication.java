package ru.boraldan.aop.taskaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(ru.boraldan.logaopstarter.starter.config.LoggerAopAutoConfiguration.class)
public class TaskAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskAopApplication.class, args);
    }

}
