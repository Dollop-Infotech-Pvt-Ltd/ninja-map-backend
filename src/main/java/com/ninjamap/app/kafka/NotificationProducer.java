package com.ninjamap.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.service.IOutboxNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final IOutboxNotificationService outboxService;
    private final KafkaHealthChecker kafkaHealthChecker;

    public <T> void sendMessage(String topic, T payloadObject, OutboxType type) {
        try {
            String payload = objectMapper.writeValueAsString(payloadObject);
            log.debug("Preparing to send Kafka message to topic [{}]: {}", topic, payload);

            if (kafkaHealthChecker.isKafkaAvailable()) {
                kafkaTemplate.send(topic, payload).whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka send failed for topic [{}]: {}", topic, ex.getMessage());
                        outboxService.saveToOutbox(payload, type, OutboxNotificationStatus.FAILED);
                    } else {
                        log.info("Kafka message successfully sent to topic [{}]", topic);
                    }
                });
            } else {
                log.warn("Kafka unavailable. Storing message in Outbox: {}", payload);
                outboxService.saveToOutbox(payload, type, OutboxNotificationStatus.NEW);
            }
        } catch (Exception e) {
            log.error("Failed to process Kafka payload: {}", e.getMessage());
            outboxService.saveToOutbox(payloadObject.toString(), type, OutboxNotificationStatus.FAILED);
        }
    }
}
