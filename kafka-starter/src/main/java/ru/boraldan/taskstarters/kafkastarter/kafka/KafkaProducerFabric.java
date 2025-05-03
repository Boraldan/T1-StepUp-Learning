package ru.boraldan.taskstarters.kafkastarter.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.boraldan.taskstarters.kafkastarter.config.KafkaConsumerProperties;

public class KafkaProducerFabric<T> {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerFabric.class);

    protected final KafkaTemplate<String, T> kafkaTemplate;
    private final KafkaConsumerProperties kafkaConsumerProperties;

    public KafkaProducerFabric(KafkaTemplate<String, T> kafkaTemplate, KafkaConsumerProperties kafkaConsumerProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    public void sendTo(String topic, T t) {
        try {
            kafkaTemplate.send(topic, t);
            kafkaTemplate.flush();
        } catch (Exception kafkaException) {
            log.error("Ошибка при отправке сообщения в Kafka", kafkaException);
        }
    }

    public void sendToUpdateStatus(T t) {
        this.sendTo(kafkaConsumerProperties.getTopic(), t);
    }

}
