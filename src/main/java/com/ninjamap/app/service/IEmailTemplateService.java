package com.ninjamap.app.service;

public interface IEmailTemplateService {

	String otpVerificationEmailTemplate(String otp, String otpType, String username, int expiryMinutes);

	String registrationEmailTemplate(String username);

	String emailVerificationEmailTemplate(String username, String verificationLink);

	String notificationEmailTemplate(String title, String description, String action, String date);

	String passwordUpdateNotificationEmailTemplate(String username, String updateTime);

	String loginSuccessEmailTemplate(String username, String loginTime);

}
