package com.ninjamap.app.service.impl;

import org.springframework.stereotype.Service;

import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendSmsRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

	private final RecipientService recipientService;

	public void sendSms(NotificationRequest request) {
		var user = recipientService.getUser(request.getUserId());
		var admin = recipientService.getAdmin(request.getAdminId());
		String phone = recipientService.getPhone(user, admin);

		if (phone == null || phone.isEmpty())
			return;

		String message = String.format("%s: %s (%s)", request.getTitle(), request.getDescription(),
				request.getAction());
		sendSmsToProvider(phone, message);
		log.info("Notification SMS sent to {}: {}", phone, message);
	}

	public void sendSms(SendSmsRequest request) {
		if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())
			return;
		sendSmsToProvider(request.getPhoneNumber(), request.getMessage());
		log.info("Custom SMS sent to {}: {}", request.getPhoneNumber(), request.getMessage());
	}

	private void sendSmsToProvider(String phone, String message) {
		// Integrate with Twilio/Nexmo or mock for now
		System.out.println("Sending SMS to " + phone + " → " + message);
	}
}
