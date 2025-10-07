package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.response.NotificationResponse;

public interface INotificationService {

	void saveInAppNotification(NotificationRequest request);

	ResponseEntity<List<NotificationResponse>> getNotifications(String userId, String adminId);

//	Notification mapRequestToNotification(NotificationRequest request, User user, Admin admin);

}
