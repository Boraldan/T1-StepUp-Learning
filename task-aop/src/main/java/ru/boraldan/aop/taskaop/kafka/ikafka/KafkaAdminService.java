package ru.boraldan.aop.taskaop.kafka.ikafka;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface KafkaAdminService {

    CompletableFuture<Set<String>> getAllTopic();
    CompletableFuture<String> createTopic(String name, int partitions, short replicationFactor);
    CompletableFuture<String> deleteTopic(String name);
}
