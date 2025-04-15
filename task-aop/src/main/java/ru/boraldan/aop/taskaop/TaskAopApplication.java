package ru.boraldan.aop.taskaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ru.boraldan.logaopstarter.starter.config.LoggerAopAutoConfiguration.class)
public class TaskAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskAopApplication.class, args);
    }

}
