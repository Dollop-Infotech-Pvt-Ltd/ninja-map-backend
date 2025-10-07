package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Notification;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.response.NotificationResponse;
import com.ninjamap.app.repository.INotificationRepository;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.INotificationService;
import com.ninjamap.app.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

	private final INotificationRepository notificationRepository;
	private final IAdminService adminService;
	private final IUserService userService;

	@Override
	public void saveInAppNotification(NotificationRequest request) {
		User user = null;
		Admin admin = null;

		if (request.getUserId() != null) {
			user = userService.getUserByIdAndIsActive(request.getUserId(), true);
		} else {
			admin = adminService.getAdminByIdAndIsActive(request.getAdminId(), true);
		}

		notificationRepository.save(mapRequestToNotification(request, user, admin));
	}

//	@Override
	public Notification mapRequestToNotification(NotificationRequest request, User user, Admin admin) {
		return Notification.builder().title(request.getTitle()).description(request.getDescription())
				.type(request.getType()).action(request.getAction()).channel(request.getChannel()).user(user)
				.admin(admin).notificationPic(request.getNotificationPic()).build();
	}

	@Override
	public ResponseEntity<List<NotificationResponse>> getNotifications(String userId, String adminId) {
		List<Notification> notifications = notificationRepository.findByUserOrAdmin(userId, adminId);

		return ResponseEntity
				.ok(notifications.stream().map(this::mapToNotificationResponse).collect(Collectors.toList()));
	}

	private NotificationResponse mapToNotificationResponse(Notification notification) {
		return NotificationResponse.builder().notificationId(notification.getNotificationId())
				.title(notification.getTitle()).description(notification.getDescription()).type(notification.getType())
				.action(notification.getAction()).notificationPic(notification.getNotificationPic())
				.channel(notification.getChannel())
				.userId(notification.getUser() != null ? notification.getUser().getUserId() : null)
				.adminId(notification.getAdmin() != null ? notification.getAdmin().getAdminId() : null)
				.dateTime(notification.getCreatedDate()).build();
	}

}
