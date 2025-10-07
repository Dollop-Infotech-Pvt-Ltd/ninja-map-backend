package com.ninjamap.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.enums.NotificationChannel;
import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.service.INotificationService;
import com.ninjamap.app.service.IOutboxNotificationService;
import com.ninjamap.app.service.impl.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationListener {

	private final ObjectMapper objectMapper;
	private final INotificationService notificationService;
	private final EmailService emailService;
	private final IOutboxNotificationService outboxNotificationService;

	@KafkaListener(topics = "${app.topic.notifications-topic}")
	public void listenNotificationTopic(String payload, Acknowledgment ack) {
		processNotification(payload, ack);
	}

	@KafkaListener(topics = "${app.topic.email-notifications-topic}")
	public void listenEmailTopic(String payload, Acknowledgment ack) {
		processEmail(payload, ack);
	}

	// ---------------- HELPER METHODS -----------------

	private void processNotification(String payload, Acknowledgment ack) {
		try {
			NotificationRequest request = objectMapper.readValue(payload, NotificationRequest.class);

			// In-app notifications
			if (request.getChannel() == NotificationChannel.IN_APP
					|| request.getChannel() == NotificationChannel.BOTH) {
				notificationService.saveInAppNotification(request);
			}

			// Email notifications
			if (request.getChannel() == NotificationChannel.EMAIL || request.getChannel() == NotificationChannel.BOTH) {
				emailService.sendEmail(request);
			}

			ack.acknowledge();

		} catch (Exception ex) {
			// Save to outbox for retry
			outboxNotificationService.saveToOutbox(payload, OutboxType.NOTIFICATION, OutboxNotificationStatus.NEW);
		}
	}

	private void processEmail(String payload, Acknowledgment ack) {
		try {
			SendEmailRequest request = objectMapper.readValue(payload, SendEmailRequest.class);
			emailService.sendEmail(request);
			ack.acknowledge();
		} catch (Exception ex) {
			System.err.println("[ERROR] Failed to send email via listener, saving to outbox: " + payload);
			ex.printStackTrace();
			outboxNotificationService.saveToOutbox(payload, OutboxType.OTP_EMAIL, OutboxNotificationStatus.NEW);
		}
	}
}
