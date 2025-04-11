package ru.boraldan.aop.taskaop.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;

@Log4j2
@RequiredArgsConstructor
public class KafkaProducerFabric<T> {

    protected final KafkaTemplate<String, T> kafkaTemplate;

    public void sendTo(String topic, T t) {
        try {
            kafkaTemplate.send(topic, t);
            kafkaTemplate.flush();
        } catch (Exception kafkaException) {
            log.error("Ошибка при отправке сообщения в Kafka", kafkaException);
        }
    }

}
