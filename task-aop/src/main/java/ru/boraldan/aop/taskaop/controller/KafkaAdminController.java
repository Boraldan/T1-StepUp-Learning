package ru.boraldan.aop.taskaop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.boraldan.aop.taskaop.kafka.KafkaAdminService;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Validated
@RestController
@RequestMapping("/api/kafka/admin")
@RequiredArgsConstructor
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    @Operation(summary = "Получить список всех топиков Kafka", description = "Возвращает множество всех существующих Kafka топиков.")
    @GetMapping("/topics")
    public CompletableFuture<Set<String>> getTopics() {
        return kafkaAdminService.getAllTopic();
    }

    @Operation(summary = "Создать новый Kafka топик", description = "Создает Kafka топик с заданными параметрами: имя, количество партиций и фактор репликации.")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<String> createTopic(@RequestParam @NotBlank String name,
                                                 @RequestParam(required = false, defaultValue = "1") @Positive int partitions,
                                                 @RequestParam(required = false, defaultValue = "1") @Positive short replicationFactor) {
        return kafkaAdminService.createTopic(name, partitions, replicationFactor);
    }

    @Operation(summary = "Удалить Kafka топик", description = "Удаляет Kafka топик по имени.")
    @DeleteMapping("/delete")
    public CompletableFuture<String> deleteTopic(@RequestParam @NotBlank String name) {
        return kafkaAdminService.deleteTopic(name);
    }
}
