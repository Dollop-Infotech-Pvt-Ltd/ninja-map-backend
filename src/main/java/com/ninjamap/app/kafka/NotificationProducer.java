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
	private final ObjectMapper objectMapper; // Spring Boot auto-configures this
	private final IOutboxNotificationService outboxService;
	private final KafkaHealthChecker kafkaHealthChecker;

	public <T> void sendMessage(String topic, T payloadObject, OutboxType type) {
		try {
			String payload = objectMapper.writeValueAsString(payloadObject);

			System.err.println("PAYLOAD IN PRODUCER ==> " + payload);
			if (kafkaHealthChecker.isKafkaAvailable()) {
				kafkaTemplate.send(topic, payload).whenComplete((result, ex) -> {
					if (ex != null) {
						outboxService.saveToOutbox(payload, type, OutboxNotificationStatus.FAILED);
					}
				});
			} else {
				System.err.println("STORE IN OUTBOX IN PRODUCER ELSE  ==> " + payload);
				outboxService.saveToOutbox(payload, type, OutboxNotificationStatus.NEW);
			}
		} catch (Exception e) {
			System.err.println("STORE IN OUTBOX IN PRODUCER EXCEPTION  ==> " + payloadObject.toString());
			outboxService.saveToOutbox(payloadObject.toString(), type, OutboxNotificationStatus.FAILED);
		}
	}
}
