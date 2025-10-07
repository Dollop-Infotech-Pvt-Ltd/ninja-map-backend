package com.ninjamap.app.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.EmailTemplate;
import com.ninjamap.app.repository.IEmailTemplateRepository;
import com.ninjamap.app.service.IEmailTemplateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements IEmailTemplateService {

	private final IEmailTemplateRepository templateRepository;

	private static final String SUPPORT_URL = "https://example.com/support";
	private static final String LOGIN_URL = "https://example.com/login";

	// Template Names
	private static final String OTP_VERIFICATION_TEMPLATE = "otp_verification";
	private static final String REGISTRATION_TEMPLATE = "registration";
	private static final String EMAIL_VERIFICATION_TEMPLATE = "email_verification";
	private static final String NOTIFICATION_TEMPLATE = "notification";
	private static final String PASSWORD_UPDATE_TEMPLATE = "password_update_notification";
	private static final String LOGIN_SUCCESS_TEMPLATE = "login_success_notification";

	private String getProcessedTemplate(String templateName, Map<String, String> placeholders) {
		EmailTemplate template = templateRepository.findByTemplateName(templateName)
				.orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateName));

		String body = template.getBody();

		// Replace placeholders dynamically
		for (Map.Entry<String, String> entry : placeholders.entrySet()) {
			body = body.replace("{{" + entry.getKey() + "}}", entry.getValue());
		}

		return body;
	}

	private Map<String, String> createPlaceholders(Object[][] keyValues) {
		Map<String, String> placeholders = new HashMap<>();
		for (Object[] kv : keyValues) {
			placeholders.put((String) kv[0], kv[1].toString());
		}
		return placeholders;
	}

	@Override
	public String otpVerificationEmailTemplate(String otp, String otpType, String username, int expiryMinutes) {
		return getProcessedTemplate(OTP_VERIFICATION_TEMPLATE,
				createPlaceholders(new Object[][] { { "user_name", username }, { "otp", otp }, { "otp_type", otpType },
						{ "otp_expiry", expiryMinutes }, { "support_url", SUPPORT_URL } }));
	}

	@Override
	public String registrationEmailTemplate(String username) {
		return getProcessedTemplate(REGISTRATION_TEMPLATE, createPlaceholders(new Object[][] {
				{ "user_name", username }, { "login_url", LOGIN_URL }, { "support_url", SUPPORT_URL } }));
	}

	@Override
	public String emailVerificationEmailTemplate(String username, String verificationLink) {
		return getProcessedTemplate(EMAIL_VERIFICATION_TEMPLATE, createPlaceholders(new Object[][] {
				{ "user_name", username }, { "verification_url", verificationLink }, { "support_url", SUPPORT_URL } }));
	}

	@Override
	public String notificationEmailTemplate(String title, String description, String action, String date) {
		return getProcessedTemplate(NOTIFICATION_TEMPLATE,
				createPlaceholders(new Object[][] { { "title", title }, { "description", description },
						{ "action", action }, { "date", date }, { "support_url", SUPPORT_URL } }));
	}

	@Override
	public String passwordUpdateNotificationEmailTemplate(String username, String updateTime) {
		return getProcessedTemplate(PASSWORD_UPDATE_TEMPLATE,
				createPlaceholders(new Object[][] { { "user_name", username }, { "update_time", updateTime },
						{ "login_url", LOGIN_URL }, { "support_url", SUPPORT_URL } }));
	}

	@Override
	public String loginSuccessEmailTemplate(String username, String loginTime) {
		return getProcessedTemplate(LOGIN_SUCCESS_TEMPLATE, createPlaceholders(new Object[][] {
				{ "user_name", username }, { "login_time", loginTime }, { "support_url", SUPPORT_URL } }));
	}
}
