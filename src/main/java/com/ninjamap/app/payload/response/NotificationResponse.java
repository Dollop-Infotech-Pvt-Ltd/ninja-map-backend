package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import com.ninjamap.app.enums.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

	private String notificationId;
	private String title;
	private String description;
	private String type;
	private String action;
	private String notificationPic;
	private NotificationChannel channel;
	private String userId;
	private String adminId;
	private LocalDateTime dateTime;
}
