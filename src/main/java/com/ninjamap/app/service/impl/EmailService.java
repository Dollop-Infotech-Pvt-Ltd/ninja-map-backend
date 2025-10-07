package com.ninjamap.app.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Notification;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.IEmailTemplateService;
import com.ninjamap.app.service.INotificationService;
import com.ninjamap.app.service.IUserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final IEmailTemplateService templateService;
	private final IUserService userService;
	private final IAdminService adminService;
	private final INotificationService notificationService;

	private static final String FROM_EMAIL = "no-reply@yourdomain.com";

	// ---------------- GENERIC NOTIFICATION ----------------
	public void sendEmail(NotificationRequest request) {
		User user = getUserIfExists(request.getUserId());
		Admin admin = getAdminIfExists(request.getAdminId());

		if (user == null && admin == null) {
			log.warn("No recipient found for NotificationRequest: {}", request);
			return;
		}

//		Notification notification = notificationService.mapRequestToNotification(request, user, admin);
		String recipient = user != null ? user.getEmail() : admin.getEmail();
		String emailBody = templateService.notificationEmailTemplate(request.getTitle(), request.getDescription(),
				request.getAction(), request.getDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));

		sendEmailSMTP(recipient, request.getTitle(), emailBody);
		log.info("Notification email sent to {}", recipient);
	}

	// ---------------- SEND OTP / REGISTRATION / PASSWORD EMAIL ----------------
	public void sendEmail(SendEmailRequest request) {
		String recipient = request.getTo();
		String subject;
		String body;

		switch (request.getTemplateType()) {
		case OTP_VERIFICATION -> {
			subject = "Your OTP Code - Action Required";
			body = templateService.otpVerificationEmailTemplate(request.getOtp(),
					request.getOtpType().toFriendlyString(), request.getUsername(), 5);
		}
		case REGISTRATION -> {
			subject = "Welcome to Ninja-Map Platform";
			body = templateService.registrationEmailTemplate(request.getUsername());
		}
		case EMAIL_VERIFICATION -> {
			subject = "Verify Your Email Address";
			body = templateService.emailVerificationEmailTemplate(request.getUsername(), "https://example.com");
		}
		case NOTIFICATION -> {
			subject = "Notification from Ninja-Map Platform";
			body = templateService.notificationEmailTemplate("Notification", "You have a new notification.", "View Now",
					request.getDataTime());
		}
		case PASSWORD_UPDATE_NOTIFICATION -> {
			subject = "Your Password Has Been Updated Successfully";
			body = templateService.passwordUpdateNotificationEmailTemplate(request.getUsername(),
					request.getDataTime());
		}
		case LOGIN_SUCCESS_NOTIFICATION -> {
			subject = "Login Successful - Ninja-Map";
			body = templateService.loginSuccessEmailTemplate(request.getUsername(), request.getDataTime());
		}
		default -> throw new BadRequestException("Unexpected email template type: " + request.getTemplateType());
		}

		sendEmailSMTP(recipient, subject, body);
		log.info("Email sent to {} with template {}", recipient, request.getTemplateType());
	}

	// ---------------- HELPER METHODS ----------------
	private User getUserIfExists(String userId) {
		return userId != null ? userService.getUserByIdAndIsActive(userId, true) : null;
	}

	private Admin getAdminIfExists(String adminId) {
		return adminId != null ? adminService.getAdminByIdAndIsActive(adminId, true) : null;
	}

	private void sendEmailSMTP(String to, String subject, String htmlBody) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(FROM_EMAIL);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true); // HTML content
			mailSender.send(message);
		} catch (MessagingException e) {
			log.error("Failed to send email to {}: {}", to, e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}
}
