package ru.boraldan.aop.taskaop.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Headers;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorKafkaMessageDeserializer<T> extends JsonDeserializer<T> {

    private String getMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @AfterThrowing
    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.warn("Произошла ошибка во время десериализации сообщения {}", this.getMessage(data), e);
            return null;
        }
    }

    @AfterThrowing
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.warn("Произошла ошибка во время десериализации сообщения {}", this.getMessage(data), e);
            return null;
        }
    }

}