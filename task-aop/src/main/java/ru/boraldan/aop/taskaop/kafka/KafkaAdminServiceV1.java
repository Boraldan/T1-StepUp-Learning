package ru.boraldan.aop.taskaop.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.stereotype.Service;
import ru.boraldan.aop.taskaop.controller.exception.KafkaAdminException;
import ru.boraldan.aop.taskaop.kafka.ikafka.KafkaAdminService;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class KafkaAdminServiceV1 implements KafkaAdminService {

    private final AdminClient adminClient;

    public CompletableFuture<Set<String>> getAllTopic() {
        KafkaFuture<Set<String>> kafkaFuture = adminClient.listTopics().names();
        CompletableFuture<Set<String>> future = new CompletableFuture<>();
        kafkaFuture.whenComplete((topics, ex) -> {
            if (ex != null) {
                future.completeExceptionally(new KafkaAdminException("Ошибка при получении топиков"));
            } else {
                future.complete(topics);
            }
        });
        return future;
    }

    public CompletableFuture<String> createTopic(String name, int partitions, short replicationFactor) {
        NewTopic newTopic = new NewTopic(name, partitions, replicationFactor);
        KafkaFuture<Void> kafkaFuture = adminClient.createTopics(Collections.singletonList(newTopic)).all();
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaFuture.whenComplete((res, ex) -> {
            if (ex != null) {
                future.completeExceptionally(new KafkaAdminException("Ошибка при создании топика"));
            } else {
                future.complete(("Топик создан : " + name));
            }
        });
        return future;
    }

    public CompletableFuture<String> deleteTopic(String name) {
        KafkaFuture<Void> kafkaFuture = adminClient.deleteTopics(Collections.singletonList(name)).all();
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaFuture.whenComplete((res, ex) -> {
            if (ex != null) {
                future.completeExceptionally(new KafkaAdminException("Ошибка при удалении топика"));
            } else {
                future.complete(("Топик удален : " + name));
            }
        });
        return future;
    }

}
