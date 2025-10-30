package com.ninjamap.app.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {
	@Value("${app.topic.notifications-topic}")
	private String notificationTopic;

	@Value("${app.topic.email-notifications-topic}")
	private String emailNotificationTopic;
	
	@Value("${app.topic.sms-notifications-topic}")
	private String smsNotificationTopic;

	public String getNotificationTopic() {
		return notificationTopic;
	}

	public String getEmailNotificationTopic() {
		return emailNotificationTopic;
	}
	
	
	public String getSmsNotificationTopic() {
		return smsNotificationTopic;
	}
}
