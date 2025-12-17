package com.ninjamap.app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import org.hibernate.validator.constraints.UUID;
import com.ninjamap.app.enums.NotificationChannel;
import com.ninjamap.app.utils.constants.ValidationConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Notification extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@UUID
	@Column(nullable = false, unique = true)
	private String notificationId;

	@Column(nullable = false)
	@NotBlank(message = ValidationConstants.TITLE_REQUIRED)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	@NotBlank(message = ValidationConstants.DESCRIPTION_REQUIRED)
	private String description;

	@Column(nullable = false)
	private String type; // CATEGORY_UPDATE, SYSTEM_ALERT

	@Column(nullable = false)
	private String action; // CREATED, UPDATED

	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''")
	private String notificationPic;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationChannel channel; // EMAIL / IN_APP / BOTH

	@ManyToOne
	@JoinColumn(name = "admin_id")
	private Admin admin;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
