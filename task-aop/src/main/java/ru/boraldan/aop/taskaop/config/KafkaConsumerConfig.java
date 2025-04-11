package ru.boraldan.aop.taskaop.config;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.kafka.ErrorKafkaMessageDeserializer;


import java.util.HashMap;
import java.util.Map;

@Log4j2
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.max-pull-interval}")
    private String maxPullIntervalMs;

    @Bean
    public ConsumerFactory<String, TasksDto> tasksDtoConsumerFactory(JsonDeserializer<TasksDto> tasksDtoJsonDeserializer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPullIntervalMs);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorKafkaMessageDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorKafkaMessageDeserializer.class.getName());
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), tasksDtoJsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TasksDto>> tasksDtoListenerContainerFactory(
            ConsumerFactory<String, TasksDto> tasksDtoConsumerFactory, CommonErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TasksDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tasksDtoConsumerFactory);
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public JsonDeserializer<TasksDto> tasksDtoJsonDeserializer() {
        JsonDeserializer<TasksDto> deserializer = new JsonDeserializer<>(TasksDto.class, false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);
        return deserializer;
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("RetryListeners message = {}, offset = {} deliveryAttempt = {}",
                    ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return handler;
    }

}

