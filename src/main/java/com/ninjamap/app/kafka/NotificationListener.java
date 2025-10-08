package com.ninjamap.app.kafka;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.enums.NotificationChannel;
import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.payload.request.SendSmsRequest;
import com.ninjamap.app.service.INotificationService;
import com.ninjamap.app.service.IOutboxNotificationService;
import com.ninjamap.app.service.impl.EmailService;
import com.ninjamap.app.service.impl.SmsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

	private final ObjectMapper objectMapper;
	private final INotificationService notificationService;
	private final EmailService emailService;
	private final SmsService smsService;
	private final IOutboxNotificationService outboxNotificationService;

	private boolean matches(NotificationChannel channel, NotificationChannel... targets) {
		return Arrays.asList(targets).contains(channel);
	}

	/**
	 * Generic method to handle Kafka messages
	 */
	private <T> void handleKafkaMessage(String payload, Acknowledgment ack, Class<T> clazz, OutboxType outboxType,
			Consumer<T> processor) {
		try {
			T request = objectMapper.readValue(payload, clazz);
			processor.accept(request);
			ack.acknowledge();
		} catch (Exception ex) {
			log.error("Failed to process message for {}: {}", outboxType, ex.getMessage());
			outboxNotificationService.saveToOutbox(payload, outboxType, OutboxNotificationStatus.NEW);
		}
	}

	@KafkaListener(topics = "${app.topic.notifications-topic}")
	public void listenNotificationTopic(String payload, Acknowledgment ack) {
		handleKafkaMessage(payload, ack, NotificationRequest.class, OutboxType.IN_APP, request -> {
			NotificationChannel channel = request.getChannel();

			if (matches(channel, NotificationChannel.IN_APP, NotificationChannel.EMAIL_IN_APP,
					NotificationChannel.IN_APP_SMS, NotificationChannel.ALL))
				notificationService.saveInAppNotification(request);

			if (matches(channel, NotificationChannel.EMAIL, NotificationChannel.EMAIL_IN_APP,
					NotificationChannel.EMAIL_SMS, NotificationChannel.ALL))
				emailService.sendEmail(request);

			if (matches(channel, NotificationChannel.SMS, NotificationChannel.IN_APP_SMS, NotificationChannel.EMAIL_SMS,
					NotificationChannel.ALL))
				smsService.sendSms(request);
		});
	}

	@KafkaListener(topics = "${app.topic.email-notifications-topic}")
	public void listenEmailTopic(String payload, Acknowledgment ack) {
		handleKafkaMessage(payload, ack, SendEmailRequest.class, OutboxType.EMAIL, emailService::sendEmail);
	}

	@KafkaListener(topics = "${app.topic.sms-notifications-topic}")
	public void listenSmsTopic(String payload, Acknowledgment ack) {
		handleKafkaMessage(payload, ack, SendSmsRequest.class, OutboxType.SMS, smsService::sendSms);
	}
}
