-- =====================================================================
--  Email Template Seed Data for NinjaMap
--  Database: PostgreSQL 17+
--  Author: Sanjana
-- =====================================================================

-- Enable pgcrypto (for UUID support)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Clean up duplicates only for development (optional)
-- DELETE FROM email_templates WHERE template_name IN (
--   'otp_verification', 'registration', 'email_verification', 
--   'notification', 'password_update_notification', 'login_success_notification'
-- );

INSERT INTO email_templates (template_id, template_name, body)
VALUES
-- ======================= OTP VERIFICATION =======================
(gen_random_uuid(), 'otp_verification',
'<!-- OTP Verification Email -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>OTP Verification</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f9f9f9; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #0056b3; color: #ffffff; padding: 20px; font-size: 26px; border-radius: 10px 10px 0 0; font-weight: bold; }
.content { padding: 30px; font-size: 18px; color: #333333; line-height: 1.6; }
.otp { font-size: 30px; font-weight: bold; color: #d9534f; background: #fdecea; padding: 12px 25px; border-radius: 5px; display: inline-block; }
.footer { text-align: center; padding: 15px; font-size: 15px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">OTP Verification</div>
<div class="content">
<p>Dear {{user_name}},</p>
<p>Your One-Time Password (OTP) for <strong>{{otp_type}}</strong> is:</p>
<p class="otp">{{otp}}</p>
<p>This code will expire in {{otp_expiry}} minutes. Please do not share it with anyone.</p>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | Need Help? <a href="{{support_url}}">Contact Support</a>
</div>
</div>
</body>
</html>'),

-- ======================= REGISTRATION CONFIRMATION =======================
(gen_random_uuid(), 'registration',
'<!-- Registration Confirmation Email -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Registration Confirmation</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f9f9f9; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #218838; color: #ffffff; padding: 20px; font-size: 26px; border-radius: 10px 10px 0 0; font-weight: bold; }
.content { padding: 30px; font-size: 18px; color: #333333; line-height: 1.6; }
.button { display: inline-block; padding: 15px 30px; background: #218838; color: #ffffff; text-decoration: none; border-radius: 6px; font-size: 18px; font-weight: bold; }
.footer { text-align: center; padding: 15px; font-size: 15px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">Welcome, {{user_name}}!</div>
<div class="content">
<p>Congratulations! Your account has been successfully created.</p>
<p>Start exploring our platform by clicking the button below:</p>
<a href="{{login_url}}" class="button">Login to Your Account</a>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | <a href="{{support_url}}">Support</a>
</div>
</div>
</body>
</html>'),

-- ======================= EMAIL VERIFICATION =======================
(gen_random_uuid(), 'email_verification',
'<!-- Email Verification -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Email Verification</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f9f9f9; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #138496; color: #ffffff; padding: 20px; font-size: 26px; border-radius: 10px 10px 0 0; font-weight: bold; }
.content { padding: 30px; font-size: 18px; color: #333333; line-height: 1.6; }
.button { display: inline-block; padding: 15px 30px; background: #138496; color: #ffffff; text-decoration: none; border-radius: 6px; font-size: 18px; font-weight: bold; }
.footer { text-align: center; padding: 15px; font-size: 15px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">Verify Your Email</div>
<div class="content">
<p>Hi {{user_name}},</p>
<p>We need to confirm your email address to activate your account.</p>
<a href="{{verification_url}}" class="button">Verify Email</a>
<p>If you didn''t create this account, you can ignore this email.</p>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | <a href="{{support_url}}">Help & Support</a>
</div>
</div>
</body>
</html>'),

-- ======================= NOTIFICATION =======================
(gen_random_uuid(), 'notification',
'<!-- General Notification -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Notification</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f9f9f9; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #6c757d; color: #ffffff; padding: 20px; font-size: 26px; border-radius: 10px 10px 0 0; font-weight: bold; }
.content { padding: 30px; font-size: 18px; color: #333333; line-height: 1.6; }
.footer { text-align: center; padding: 15px; font-size: 15px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">{{title}}</div>
<div class="content">
<p>{{description}}</p>
<p>Action: {{action}}</p>
<p>Date: {{date}}</p>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | <a href="{{support_url}}">Help & Support</a>
</div>
</div>
</body>
</html>'),

-- ======================= PASSWORD UPDATE =======================
(gen_random_uuid(), 'password_update_notification',
'<!-- Password Reset Success -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Password Reset Successful</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f4f9f4; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #28a745; color: #ffffff; padding: 20px; font-size: 24px; font-weight: bold; border-radius: 10px 10px 0 0; }
.content { padding: 30px; font-size: 16px; color: #333333; line-height: 1.6; }
.success { font-size: 20px; font-weight: bold; color: #28a745; margin: 20px 0; }
.footer { text-align: center; padding: 15px; font-size: 14px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">Password Reset Successful</div>
<div class="content">
<p>Dear {{user_name}},</p>
<p class="success">Your password has been updated successfully!</p>
<p>If this was not you, please <a href="{{support_url}}">contact support immediately</a> to secure your account.</p>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | Stay Secure 🔐
</div>
</div>
</body>
</html>'),

-- ======================= LOGIN SUCCESS =======================
(gen_random_uuid(), 'login_success_notification',
'<!-- Login Successful Notification -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Login Successful</title>
<style>
body { font-family: Arial, sans-serif; background-color: #f4f9f4; }
.container { max-width: 600px; margin: 20px auto; background: #ffffff; padding: 25px; border-radius: 10px; text-align: center; }
.header { background: #007bff; color: #ffffff; padding: 20px; font-size: 24px; font-weight: bold; border-radius: 10px 10px 0 0; }
.content { padding: 30px; font-size: 16px; color: #333333; line-height: 1.6; }
.success { font-size: 20px; font-weight: bold; color: #007bff; margin: 20px 0; }
.footer { text-align: center; padding: 15px; font-size: 14px; color: #666666; background: #ececec; border-radius: 0 0 10px 10px; }
</style>
</head>
<body>
<div class="container">
<div class="header">Login Successful</div>
<div class="content">
<p>Dear {{user_name}},</p>
<p class="success">You have successfully logged in to your account!</p>
<p>If this wasn''t you, please <a href="{{support_url}}">contact support immediately</a> to secure your account.</p>
<p>Login Time: {{login_time}}</p>
</div>
<div class="footer">
&copy; 2025 Ninja-Map | Stay Secure 🔐
</div>
</div>
</body>
</html>');
