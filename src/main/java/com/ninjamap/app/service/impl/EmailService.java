package com.ninjamap.app.service.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.payload.request.NotificationRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.service.IEmailTemplateService;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final IEmailTemplateService templateService;
	private final RecipientService recipientService;

	private static final String FROM_EMAIL = AppConstants.FROM_EMAIL;

	public void sendEmail(NotificationRequest request) {
		var user = recipientService.getUser(request.getUserId());
		var admin = recipientService.getAdmin(request.getAdminId());
		String recipient = recipientService.getEmail(user, admin);

		if (recipient == null) {
			log.warn("No recipient found for NotificationRequest: {}", request);
			return;
		}

		String body = templateService.notificationEmailTemplate(request.getTitle(), request.getDescription(),
				request.getAction(), request.getDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));

		sendEmailSMTP(recipient, request.getTitle(), body);
		log.info("Notification email sent to {}", recipient);
	}

	public void sendEmail(SendEmailRequest request) {
		String subject;
		String body;

		switch (request.getTemplateType()) {
		case OTP_VERIFICATION -> subject = AppConstants.OTP_VERIFICATION_SUBJECT;
		case REGISTRATION -> subject = AppConstants.REGISTRATION_SUBJECT;
		case EMAIL_VERIFICATION -> subject = AppConstants.EMAIL_VERIFICATION_SUBJECT;
		case NOTIFICATION -> subject = AppConstants.NOTIFICATION_SUBJECT;
		case PASSWORD_UPDATE_NOTIFICATION -> subject = AppConstants.PASSWORD_UPDATE_NOTIFICATION_SUBJECT;
		case LOGIN_SUCCESS_NOTIFICATION -> subject = AppConstants.LOGIN_SUCCESS_NOTIFICATION_SUBJECT;
		default -> throw new BadRequestException("Unexpected email template type: " + request.getTemplateType());
		}

		body = switch (request.getTemplateType()) {
		case OTP_VERIFICATION -> templateService.otpVerificationEmailTemplate(request.getOtp(),
				request.getOtpType().toFriendlyString(), request.getUsername(), 5);
		case REGISTRATION -> templateService.registrationEmailTemplate(request.getUsername());
		case EMAIL_VERIFICATION ->
			templateService.emailVerificationEmailTemplate(request.getUsername(), "https://example.com");
		case NOTIFICATION -> templateService.notificationEmailTemplate("Notification", "You have a new notification.",
				"View Now", request.getDataTime());
		case PASSWORD_UPDATE_NOTIFICATION ->
			templateService.passwordUpdateNotificationEmailTemplate(request.getUsername(), request.getDataTime());
		case LOGIN_SUCCESS_NOTIFICATION ->
			templateService.loginSuccessEmailTemplate(request.getUsername(), request.getDataTime());
		default -> throw new BadRequestException("Unexpected email template type: " + request.getTemplateType());
		};

		sendEmailSMTP(request.getTo(), subject, body);
		log.info("Email sent to {} with template {}", request.getTo(), request.getTemplateType());
	}

	private void sendEmailSMTP(String to, String subject, String htmlBody) {
		try {
			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, "UTF-8");
			helper.setFrom(FROM_EMAIL);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			log.error("Failed to send email to {}: {}", to, e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}
}
