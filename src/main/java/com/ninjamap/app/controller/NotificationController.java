package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.service.INotificationService;
import com.ninjamap.app.utils.constants.AppConstants;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private INotificationService notificationService;

	// For Both Admin and User Notification
	@GetMapping("/get")
	public ResponseEntity<?> getNotifications(
			@RequestParam(name = AppConstants.USER_ID, required = false) String userId,
			@RequestParam(name = AppConstants.ADMIN_ID, required = false) String adminId) {
		return notificationService.getNotifications(userId, adminId);
	}
}
