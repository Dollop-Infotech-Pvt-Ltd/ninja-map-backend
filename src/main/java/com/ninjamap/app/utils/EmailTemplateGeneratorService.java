//package com.ninjamap.app.utils;
//
//import org.springframework.stereotype.Service;
//
//import com.ninjamap.app.enums.OtpType;
//import com.ninjamap.app.model.Notification;
//import com.ninjamap.app.utils.constants.AppConstants;
//
//@Service
//public class EmailTemplateGeneratorService {
//
//	public String generateOtpEmail(String userName, String otp, OtpType otpType) {
//		return """
//				<!DOCTYPE html>
//				<html>
//				<head>
//				    <meta charset="UTF-8">
//				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//				    <style>
//				        body {
//				            font-family: Arial, sans-serif;
//				            background-color: #f4f6f8;
//				            margin: 0;
//				            padding: 0;
//				            color: #333;
//				        }
//				        .container {
//				            max-width: 600px;
//				            margin: 20px auto;
//				            background: #ffffff;
//				            border-radius: 8px;
//				            overflow: hidden;
//				            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
//				        }
//				        .header {
//				            background-color: #2D89EF;
//				            color: #ffffff;
//				            text-align: center;
//				            padding: 20px;
//				        }
//				        .header h2 {
//				            margin: 0;
//				            font-size: 22px;
//				        }
//				        .content {
//				            padding: 20px;
//				            font-size: 16px;
//				            line-height: 1.5;
//				        }
//				        .otp-box {
//				            text-align: center;
//				            margin: 20px 0;
//				        }
//				        .otp {
//				            display: inline-block;
//				            font-size: 24px;
//				            font-weight: bold;
//				            color: #2D89EF;
//				            padding: 10px 20px;
//				            border: 2px dashed #2D89EF;
//				            border-radius: 6px;
//				            background-color: #f0f7ff;
//				        }
//				        .footer {
//				            text-align: center;
//				            padding: 15px;
//				            font-size: 13px;
//				            color: #777;
//				            background-color: #f9f9f9;
//				            border-top: 1px solid #eee;
//				        }
//				        @media only screen and (max-width: 600px) {
//				            .content { font-size: 15px; padding: 15px; }
//				            .otp { font-size: 20px; padding: 8px 16px; }
//				        }
//				    </style>
//				</head>
//				<body>
//				    <div class="container">
//				        <div class="header">
//				            <h2>%s</h2>
//				        </div>
//				        <div class="content">
//				            <p>Dear <strong>%s</strong>,</p>
//				            <p>You recently requested a One-Time Password (OTP) for <strong>%s</strong> on your %s account.</p>
//				            <div class="otp-box">
//				                <span class="otp">%s</span>
//				            </div>
//				            <p>Please use this OTP to complete your action. This code is valid for <strong>5 minutes</strong>.
//				            Do not share this code with anyone.</p>
//				            <p>If you did not request this OTP, please ignore this email or contact our support team immediately.</p>
//				        </div>
//				        <div class="footer">
//				            <p>Best regards,<br><strong>%s Team</strong></p>
//				            <p>Contact us: <a href="%s">%s</a></p>
//				        </div>
//				    </div>
//				</body>
//				</html>
//				"""
//				.formatted(AppConstants.COMPANY_NAME, userName, AppUtils.formatOtpType(otpType),
//						AppConstants.COMPANY_NAME, otp, AppConstants.COMPANY_NAME, AppConstants.COMPANY_SUPPORT_LINK,
//						AppConstants.COMPANY_EMAIL);
//	}
//
//	/**
//	 * Generates HTML email content for a notification Supports both User and Admin
//	 * recipients
//	 */
//	public String generateNotificationEmail(Notification notification) {
//		String recipientName = notification.getUser() != null
//				? notification.getUser().getFirstName() + " " + notification.getUser().getLastName()
//				: notification.getAdmin() != null
//						? notification.getAdmin().getFirstName() + " " + notification.getAdmin().getLastName()
//						: "User";
//
//		return """
//				<!DOCTYPE html>
//				<html>
//				<head>
//				    <meta charset="UTF-8">
//				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//				    <style>
//				        body { font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0; color: #333; }
//				        .container { max-width: 600px; margin: 20px auto; background: #fff; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
//				        .header { background-color: #2D89EF; color: #fff; text-align: center; padding: 20px; }
//				        .header h2 { margin: 0; font-size: 22px; }
//				        .content { padding: 20px; font-size: 16px; line-height: 1.5; }
//				        .footer { text-align: center; padding: 15px; font-size: 13px; color: #777; background-color: #f9f9f9; border-top: 1px solid #eee; }
//				        @media only screen and (max-width: 600px) { .content { font-size: 15px; padding: 15px; } }
//				    </style>
//				</head>
//				<body>
//				    <div class="container">
//				        <div class="header">
//				            <h2>%s</h2>
//				        </div>
//				        <div class="content">
//				            <p>Dear %s,</p>
//				            <p>%s</p>
//				            <p><strong>Type:</strong> %s</p>
//				            <p><strong>Action:</strong> %s</p>
//				            <p>Date: %s | Time: %s</p>
//				        </div>
//				        <div class="footer">
//				            <p>Best regards,<br><strong>%s Team</strong></p>
//				            <p>Contact us: <a href="%s">%s</a></p>
//				        </div>
//				    </div>
//				</body>
//				</html>
//				"""
//				.formatted(AppConstants.COMPANY_NAME, recipientName, notification.getDescription(),
//						notification.getType(), notification.getAction(),
//						notification.getCreatedDate() != null ? notification.getCreatedDate().toLocalDate().toString()
//								: "-",
//						notification.getCreatedDate() != null ? notification.getCreatedDate().toLocalTime().toString()
//								: "-",
//						AppConstants.COMPANY_NAME, AppConstants.COMPANY_SUPPORT_LINK, AppConstants.COMPANY_EMAIL);
//	}
//}
