package com.ninjamap.app.model;

import org.hibernate.validator.constraints.UUID;

import com.ninjamap.app.enums.OutboxNotificationStatus;
import com.ninjamap.app.enums.OutboxType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "outbox_notifications")
public class OutboxNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@UUID
	@Column(nullable = false, unique = true)
	private String id;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String payload; // JSON string of NotificationRequest

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxNotificationStatus status; // NEW, FAILED

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxType type;
}
