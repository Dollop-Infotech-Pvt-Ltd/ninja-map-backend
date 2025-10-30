package com.ninjamap.app.publisher;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.kafka.KafkaTopics;
import com.ninjamap.app.kafka.NotificationProducer;
import com.ninjamap.app.model.OutboxNotification;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.payload.request.SendSmsRequest;
import com.ninjamap.app.repository.IOutboxNotificationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

	private final IOutboxNotificationRepository outboxNotificationRepository;
	private final NotificationProducer notificationProducer;
	private final ObjectMapper objectMapper;
	private final KafkaTopics kafkaTopics;

//	@Scheduled(fixedDelay = 60000)
	public void retryOutbox() {
		List<OutboxNotification> pending = outboxNotificationRepository
				.findByStatusIn(List.of(OutboxNotificationStatus.NEW, OutboxNotificationStatus.FAILED));

		for (OutboxNotification entry : pending) {
			try {
				sendMessage(entry);
				outboxNotificationRepository.delete(entry); // delete after successful send
			} catch (Exception e) {
				entry.setStatus(OutboxNotificationStatus.FAILED);
				outboxNotificationRepository.save(entry);
			}
		}
	}

	private void sendMessage(OutboxNotification entry) throws Exception {
		OutboxType type = entry.getType();

		switch (type) {
		case IN_APP -> notificationProducer.sendMessage(kafkaTopics.getNotificationTopic(),
				objectMapper.readValue(entry.getPayload(), NotificationRequest.class), OutboxType.IN_APP);
		case EMAIL -> notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(),
				objectMapper.readValue(entry.getPayload(), SendEmailRequest.class), OutboxType.EMAIL);
		case SMS -> notificationProducer.sendMessage(kafkaTopics.getSmsNotificationTopic(),
				objectMapper.readValue(entry.getPayload(), SendSmsRequest.class), OutboxType.SMS);
		default -> throw new BadRequestException("Unsupported OutboxType: " + type);
		}
	}
}
