package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.kafka.KafkaTopics;
import com.ninjamap.app.kafka.NotificationProducer;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IAdminAuthService;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.IOtpService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.AuthServiceHelper;
import com.ninjamap.app.utils.DeviceMetadataUtil;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements IAdminAuthService {

	private final IAdminService adminService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final IOtpService otpService;
	private final ISessionService sessionService;
	private final ISessionRepository sessionRepository;
	private final HttpServletRequest httpRequest;
	private final AuthServiceHelper authHelper;
	private final DeviceMetadataUtil deviceMetadataUtil;
	private final NotificationProducer notificationProducer;
	private final KafkaTopics kafkaTopics;

	// -------------------- LOGIN -----------------------
	@Override
	public ApiResponse login(LoginRequest request) {
		Admin admin = adminService.getAdminByEmailAndIsActive(request.getUsername(), true);

		if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
			throw new UnauthorizedException(AppConstants.INVALID_CREDENTIALS);
		}

		return generateAuthTokenWithOtp(admin, OtpType.LOGIN, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- VERIFY OTP -----------------------
	@Override
	public ApiResponse verifyOtp(OtpRequest otpRequest) {
		String token = jwtUtils.getToken(httpRequest);
		validateAuthToken(token, Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD));

		String email = jwtUtils.extractEmail(token);
		OtpType otpType = jwtUtils.extractOtpType(token);

		if (!otpService.validateOtp(email, otpRequest.getOtp(), otpType)) {
			throw new BadRequestException(AppConstants.INVALID_OR_EXPIRED_OTP);
		}

		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		if (authHelper.isAuthTokenRequired(otpType)) {
			String authToken = jwtUtils.generateToken(email, admin.getRole().getRoleName(), TokenType.AUTH_TOKEN,
					otpType, true);
			return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY,
					Map.of(AppConstants.AUTH_TOKEN, authToken));
		} else {
			ApiResponse response = generateAccessAndRefreshTokens(admin, otpType);

			// Send notification email on successful login
//			if (otpType == OtpType.LOGIN) {
//				SendEmailRequest emailRequest = mapToSendEmailRequest(admin.getEmail(), admin.getRole().getRoleName(),
//						null, OtpType.LOGIN, EmailTemplateType.LOGIN_SUCCESS_NOTIFICATION);
//				notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest,
//						OutboxType.EMAIL);
//			}

			return response;
		}
	}

	// -------------------- RESEND OTP -----------------------
	@Override
	public ApiResponse resendOtp() {
		String oldToken = jwtUtils.getToken(httpRequest);
		String email = jwtUtils.extractEmail(oldToken);
		OtpType otpType = jwtUtils.extractOtpType(oldToken);
		Boolean isOtpVerified = jwtUtils.extractIsOtpVerified(oldToken);

		if (Boolean.TRUE.equals(isOtpVerified))
			throw new BadRequestException(AppConstants.OTP_ALREADY_VERIFIED);
		if (!Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD).contains(otpType))
			throw new BadRequestException(AppConstants.RESEND_OTP_NOT_ALLOWED_FOR_THIS_TYPE);

		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);
		return generateAuthTokenWithOtp(admin, otpType, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- FORGOT PASSWORD -----------------------
	@Override
	public ApiResponse forgotPassword(ForgetPasswordRequest request) {
		Admin admin = adminService.getAdminByEmailAndIsActive(request.getUsername(), true);
		return generateAuthTokenWithOtp(admin, OtpType.FORGET_PASSWORD, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- RESET PASSWORD -----------------------
	@Override
	public ApiResponse resetPassword(ResetPasswordRequest request) {
		String token = jwtUtils.getToken(httpRequest);
		validateAuthToken(token, Set.of(OtpType.FORGET_PASSWORD));

		if (!Boolean.TRUE.equals(jwtUtils.extractIsOtpVerified(token))) {
			throw new UnauthorizedException(AppConstants.OTP_NOT_VERIFIED);
		}

		String email = jwtUtils.extractEmail(token);
		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		if (passwordEncoder.matches(request.getNewPassword(), admin.getPassword())) {
			throw new BadRequestException(AppConstants.PASSWORD_SHOULD_BE_DIFFERENT);
		}

		admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
		adminService.saveAdmin(admin);

		// Send notification email for password update
//		SendEmailRequest emailRequest = mapToSendEmailRequest(admin.getEmail(), admin.getRole().getRoleName(), null,
//				OtpType.FORGET_PASSWORD, EmailTemplateType.PASSWORD_UPDATE_NOTIFICATION);
//		notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest, OutboxType.EMAIL);

		return AppUtils.buildSuccessResponse(AppConstants.PASSWORD_RESET_SUCCESS);
	}

	// -------------------- REFRESH TOKEN -----------------------
	@Override
	public ApiResponse refreshToken() {
		String refreshToken = jwtUtils.getToken(httpRequest);

		if (!jwtUtils.validateRefreshToken(refreshToken))
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN);

		Session session = sessionRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_RECOGNIZED));

		String email = jwtUtils.extractEmail(refreshToken);
		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		if (!session.getAccountId().equals(admin.getAdminId()))
			throw new ForbiddenException(AppConstants.TOKEN_NOT_BELONG_TO_USER);

		String newAccessToken = jwtUtils.generateToken(email, admin.getRole().getRoleName(), TokenType.ACCESS_TOKEN,
				OtpType.LOGIN, true);
		sessionService.updateAccessTokenForRefreshToken(admin, refreshToken, newAccessToken);

		return AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_GENERATED, newAccessToken);
	}

	// -------------------- HELPER METHODS -----------------------
	private void validateAuthToken(String token, Set<OtpType> allowedOtpTypes) {
		if (token == null)
			throw new UnauthorizedException(AppConstants.MISSING_TOKEN);
		if (jwtUtils.isTokenExpired(token))
			throw new UnauthorizedException(AppConstants.TOKEN_EXPIRED);

		TokenType tokenType = jwtUtils.extractTokenType(token);
		OtpType otpType = jwtUtils.extractOtpType(token);

		if (tokenType != TokenType.AUTH_TOKEN || !allowedOtpTypes.contains(otpType))
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN_TYPE_FOR_VERIFICATION);
	}

	private ApiResponse generateAuthTokenWithOtp(Admin admin, OtpType otpType, EmailTemplateType templateType) {
		String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole().getRoleName(), TokenType.AUTH_TOKEN,
				otpType, false);
		String otp = otpService.generateOtp(admin.getEmail(), otpType);

		// Send OTP email
//		SendEmailRequest emailRequest = mapToSendEmailRequest(admin.getEmail(), admin.getRole().getRoleName(), otp,
//				otpType, templateType);
//		notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest, OutboxType.EMAIL);

		Map<String, Object> res = Map.of(AppConstants.AUTH_TOKEN, token, AppConstants.OTP, otp);
		return AppUtils
				.buildSuccessResponse(otpType == OtpType.FORGET_PASSWORD ? AppConstants.OTP_SENT_FOR_RESET_PASSWORD
						: AppConstants.OTP_SENT_FOR_LOGIN, res);
	}

	private ApiResponse generateAccessAndRefreshTokens(Admin admin, OtpType otpType) {
		Map<String, Object> tokens = authHelper.generateAccessAndRefreshTokens(admin.getEmail(),
				admin.getRole().getRoleName(), otpType);

		String accessToken = (String) tokens.get(AppConstants.ACCESS_TOKEN);
		String refreshToken = (String) tokens.get(AppConstants.REFRESH_TOKEN);

		String userAgent = httpRequest.getHeader(AppConstants.USER_AGENT);
		String ipAddress = deviceMetadataUtil.getClientIp(httpRequest);
		sessionService.createSession(admin, accessToken, refreshToken, userAgent, ipAddress);

		return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY, tokens);
	}

//	private SendEmailRequest mapToSendEmailRequest(String email, String username, String otp, OtpType otpType,
//			EmailTemplateType templateType) {
//		return SendEmailRequest.builder().to(email).username(username).otp(otp).otpType(otpType)
//				.templateType(templateType)
//				.dataTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))).build();
//	}
}
