package com.ninjamap.app.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.kafka.KafkaTopics;
import com.ninjamap.app.kafka.NotificationProducer;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.SendEmailRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IOtpService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthServiceHelper {

	private final JwtUtils jwtUtils;
	private final IOtpService otpService;
	private final ISessionService sessionService;
	private final DeviceMetadataUtil deviceMetadataUtil;
	private final HttpServletRequest httpRequest;
	private final NotificationProducer notificationProducer;
	private final KafkaTopics kafkaTopics;

	/**
	 * Determine if an AuthToken is required based on OTP type.
	 */
	public boolean isAuthTokenRequired(OtpType otpType) {
		return resolveTokenType(otpType) == TokenType.AUTH_TOKEN;
	}

	/**
	 * Resolve the token type based on OTP type.
	 */
	public TokenType resolveTokenType(OtpType otpType) {
		return otpType == OtpType.FORGET_PASSWORD ? TokenType.AUTH_TOKEN : TokenType.ACCESS_TOKEN;
	}

	/**
	 * Generate access and refresh tokens.
	 */
	public Map<String, Object> generateAccessAndRefreshTokens(String email, String role, OtpType otpType) {
		String accessToken = jwtUtils.generateToken(email, role, resolveTokenType(otpType), otpType, true);
		String refreshToken = jwtUtils.generateRefreshToken(email, role);

		Map<String, Object> tokens = new HashMap<>();
		tokens.put(AppConstants.ACCESS_TOKEN, accessToken);
		tokens.put(AppConstants.REFRESH_TOKEN, refreshToken);
		return tokens;
	}

	// --------------------------------------------------------------------
	// AUTH TOKEN VALIDATION
	// --------------------------------------------------------------------
	public void validateAuthToken(String token, Set<OtpType> allowedOtpTypes) {
		if (token == null)
			throw new UnauthorizedException(AppConstants.MISSING_TOKEN);
		if (jwtUtils.isTokenExpired(token))
			throw new UnauthorizedException(AppConstants.TOKEN_EXPIRED);

		TokenType tokenType = jwtUtils.extractTokenType(token);
		OtpType otpType = jwtUtils.extractOtpType(token);

		if (tokenType != TokenType.AUTH_TOKEN || !allowedOtpTypes.contains(otpType))
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN_TYPE_FOR_VERIFICATION);
	}

	// --------------------------------------------------------------------
	// OTP VALIDATION FLOW
	// --------------------------------------------------------------------
	public OtpVerificationContext validateAndExtractOtp(OtpRequest otpRequest, Set<OtpType> allowedOtpTypes) {
		String token = jwtUtils.getToken(httpRequest);
		validateAuthToken(token, allowedOtpTypes);

		String email = jwtUtils.extractEmail(token);
		OtpType otpType = jwtUtils.extractOtpType(token);

		if (!otpService.validateOtp(email, otpRequest.getOtp(), otpType)) {
			throw new BadRequestException(AppConstants.INVALID_OR_EXPIRED_OTP);
		}

		return new OtpVerificationContext(email, otpType);
	}

	// --------------------------------------------------------------------
	// BUILD AUTH TOKEN RESPONSE
	// --------------------------------------------------------------------
	public ApiResponse buildAuthTokenResponse(String email, String role, OtpType otpType) {
		String authToken = jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, otpType, true);
		return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY,
				Map.of(AppConstants.AUTH_TOKEN, authToken));
	}

	// --------------------------------------------------------------------
	// GENERATE ACCESS + REFRESH + SESSION
	// --------------------------------------------------------------------
	public ApiResponse generateAccessAndRefreshTokensAndSession(String email, String role, OtpType otpType,
			Object userOrAdmin) {
		Map<String, Object> tokens = generateAccessAndRefreshTokens(email, role, otpType);

		String accessToken = (String) tokens.get(AppConstants.ACCESS_TOKEN);
		String refreshToken = (String) tokens.get(AppConstants.REFRESH_TOKEN);

		String userAgent = httpRequest.getHeader(AppConstants.USER_AGENT);
		String ipAddress = deviceMetadataUtil.getClientIp(httpRequest);

		sessionService.createSession(userOrAdmin, accessToken, refreshToken, userAgent, ipAddress);

		return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY, tokens);
	}

	// --------------------------------------------------------------------
	// GENERATE OTP + AUTH TOKEN
	// --------------------------------------------------------------------
	public ApiResponse generateOtpAndAuthToken(String identifier, String role, OtpType otpType,
			EmailTemplateType templateType) {
		String token = jwtUtils.generateToken(identifier, role, TokenType.AUTH_TOKEN, otpType, false);
		String otp = otpService.generateOtp(identifier, otpType);

		return AppUtils.buildSuccessResponse(
				otpType == OtpType.FORGET_PASSWORD ? AppConstants.OTP_SENT_FOR_RESET_PASSWORD
						: AppConstants.OTP_SENT_FOR_LOGIN,
				Map.of(AppConstants.AUTH_TOKEN, token, AppConstants.OTP, otp));
	}

	// --------------------------------------------------------------------
	// SEND OTP TO EMAIL
	// --------------------------------------------------------------------
	public void sendEmail(String email, String username, String otp, OtpType otpType, EmailTemplateType templateType) {
		SendEmailRequest emailRequest = SendEmailRequest.builder().to(email).username(username).otp(otp)
				.otpType(otpType).templateType(templateType)
				.dataTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))).build();
		notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest, OutboxType.EMAIL);
	}

	// --------------------------------------------------------------------
	// INNER RECORD FOR OTP CONTEXT
	// --------------------------------------------------------------------
	public record OtpVerificationContext(String email, OtpType otpType) {
	}
}
