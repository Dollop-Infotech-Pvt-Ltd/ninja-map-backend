package com.ninjamap.app.kafka;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHealthChecker {

	private final KafkaAdmin kafkaAdmin;

	private volatile boolean kafkaUp = true;
	private volatile long lastChecked = 0;
	private static final long CACHE_DURATION_MS = TimeUnit.SECONDS.toMillis(30);

	/**
	 * Checks if Kafka broker is available (cached for 30s)
	 */
	public synchronized boolean isKafkaAvailable() {
		long now = System.currentTimeMillis();
		if (now - lastChecked < CACHE_DURATION_MS) {
			return kafkaUp;
		}

		try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
			client.describeCluster().nodes().get();
			kafkaUp = true;
			log.debug("Kafka is available.");
		} catch (Exception e) {
			kafkaUp = false;
			log.error("Kafka is DOWN: {}", e.getMessage());
		}

		lastChecked = now;
		return kafkaUp;
	}
}
