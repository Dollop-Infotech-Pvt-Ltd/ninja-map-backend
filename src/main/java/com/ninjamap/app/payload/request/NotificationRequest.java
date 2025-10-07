package com.ninjamap.app.payload.request;

import java.time.LocalDateTime;

import com.ninjamap.app.enums.NotificationChannel;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {
	private String title;
	private String description;
	private String type; // e.g., SYSTEM_ALERT, ROLE_UPDATE
	private String action; // e.g., CREATED, UPDATED
	private NotificationChannel channel; // EMAIL / IN_APP / BOTH
	private String adminId;
	private String userId;
	private String notificationPic;
	private LocalDateTime dateTime;
}
