package com.ninjamap.app.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthChecker {

	@Autowired
	private KafkaAdmin kafkaAdmin;

	/**
	 * Checks if Kafka broker is available
	 */
	public boolean isKafkaAvailable() {
		try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
			client.describeCluster().nodes().get();
			return true;
		} catch (Exception e) {
			System.err.println("[DEBUG] Kafka is DOWN: " + e.getMessage());
			return false;
		}
	}
}
