package ru.boraldan.taskstarters.kafkastarter.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.boraldan.taskstarters.kafkastarter.kafka.ErrorKafkaMessageDeserializer;
import ru.boraldan.taskstarters.kafkastarter.kafka.KafkaProducerFabric;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableKafka
@EnableConfigurationProperties({KafkaProperties.class, KafkaConsumerProperties.class})
@ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true", matchIfMissing = true)
public class KafkaAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaAutoConfig.class);

    private final KafkaProperties kafkaProperties;
    private final KafkaConsumerProperties kafkaConsumerProperties;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return AdminClient.create(config);
    }

    @Bean
    public <T> KafkaTemplate<String, T> tasksKafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        ProducerFactory<String, T> tasksProducerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(tasksProducerFactory);
    }

    @Bean
    public <T> KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, T>> tasksDtoListenerContainerFactory() {
        DefaultErrorHandler kafkaErrorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        kafkaErrorHandler.addNotRetryableExceptions(IllegalStateException.class);
        kafkaErrorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("RetryListeners message = {}, offset = {} deliveryAttempt = {}",
                    ex.getMessage(), record.offset(), deliveryAttempt);
        });
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMaxPullInterval());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, kafkaConsumerProperties.getPayloadDto());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);

        ConsumerFactory<String, T> tasksDtoConsumerFactory = new DefaultKafkaConsumerFactory<>(configProps);

        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tasksDtoConsumerFactory);
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public <T> ErrorKafkaMessageDeserializer<T> errorKafkaMessageDeserializer() {
        return new ErrorKafkaMessageDeserializer<>();
    }

    @Bean
    public <T> KafkaProducerFabric<T> kafkaProducerFabric(KafkaTemplate<String, T> kafkaTemplate,
                                                          KafkaConsumerProperties kafkaConsumerProperties) {
        return new KafkaProducerFabric<>(kafkaTemplate, kafkaConsumerProperties);
    }


}
