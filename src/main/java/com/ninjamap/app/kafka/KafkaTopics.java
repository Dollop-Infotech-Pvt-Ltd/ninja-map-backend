package com.ninjamap.app.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {
	@Value("${app.topic.notifications-topic}")
	private String notificationTopic;

	@Value("${app.topic.email-notifications-topic}")
	private String emailNotificationTopic;

	public String getNotificationTopic() {
		return notificationTopic;
	}

	public String getEmailNotificationTopic() {
		return emailNotificationTopic;
	}
}
