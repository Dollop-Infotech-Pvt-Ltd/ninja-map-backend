package com.ninjamap.app.service.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.service.IAdminAuthService;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.AuthServiceHelper;
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
	private final ISessionService sessionService;
	private final ISessionRepository sessionRepository;
	private final HttpServletRequest httpRequest;
	private final AuthServiceHelper authHelper;

	// -------------------- LOGIN -----------------------
	@Override
	public ApiResponse login(LoginRequest request) {
		Admin admin = adminService.getAdminByEmailAndIsActive(request.getUsername(), null);
		if (Boolean.FALSE.equals(admin.getIsActive())) {
			throw new ResourceNotFoundException(AppConstants.USER_ACCOUNT_INACTIVE);
		}

		if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPersonalInfo().getPassword())) {
			throw new UnauthorizedException(AppConstants.INVALID_CREDENTIALS);
		}

		return authHelper.generateOtpAndAuthToken(admin.getPersonalInfo().getEmail(), admin.getRole().getRoleName(),
				OtpType.LOGIN, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- VERIFY OTP -----------------------
	@Override
	public ApiResponse verifyOtp(OtpRequest otpRequest) {
		var context = authHelper.validateAndExtractOtp(otpRequest, Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD));
		String email = context.email();
		OtpType otpType = context.otpType();

		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		if (authHelper.isAuthTokenRequired(otpType)) {
			return authHelper.buildAuthTokenResponse(email, admin.getRole().getRoleName(), otpType);
		} else {
			// Send notification email on successful login
//				if (otpType == OtpType.LOGIN) {
//					SendEmailRequest emailRequest = mapToSendEmailRequest(admin.getPersonalInfo().getEmail(), admin.getRole().getRoleName(),
//							null, OtpType.LOGIN, EmailTemplateType.LOGIN_SUCCESS_NOTIFICATION);
//					notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest,
//							OutboxType.EMAIL);
//				}

			return authHelper.generateAccessAndRefreshTokensAndSession(admin.getPersonalInfo().getEmail(),
					admin.getRole().getRoleName(), otpType, admin);
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
		return authHelper.generateOtpAndAuthToken(email, admin.getRole().getRoleName(), otpType,
				EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- FORGOT PASSWORD -----------------------
	@Override
	public ApiResponse forgotPassword(ForgetPasswordRequest request) {
		Admin admin = adminService.getAdminByEmailAndIsActive(request.getUsername(), true);
		return authHelper.generateOtpAndAuthToken(admin.getPersonalInfo().getEmail(), admin.getRole().getRoleName(),
				OtpType.FORGET_PASSWORD, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- RESET PASSWORD -----------------------
	@Override
	public ApiResponse resetPassword(ResetPasswordRequest request) {
		String token = jwtUtils.getToken(httpRequest);
		authHelper.validateAuthToken(token, Set.of(OtpType.FORGET_PASSWORD));

		if (!Boolean.TRUE.equals(jwtUtils.extractIsOtpVerified(token))) {
			throw new UnauthorizedException(AppConstants.OTP_NOT_VERIFIED);
		}

		String email = jwtUtils.extractEmail(token);
		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		if (passwordEncoder.matches(request.getNewPassword(), admin.getPersonalInfo().getPassword())) {
			throw new BadRequestException(AppConstants.PASSWORD_SHOULD_BE_DIFFERENT);
		}

		admin.getPersonalInfo().setPassword(passwordEncoder.encode(request.getNewPassword()));
		adminService.saveAdmin(admin);

		// Send notification email for password update
//		SendEmailRequest emailRequest = mapToSendEmailRequest(admin.getPersonalInfo().getEmail(), admin.getRole().getRoleName(), null,
//				OtpType.FORGET_PASSWORD, EmailTemplateType.PASSWORD_UPDATE_NOTIFICATION);
//		notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest, OutboxType.EMAIL);

		return AppUtils.buildSuccessResponse(AppConstants.PASSWORD_RESET_SUCCESS);
	}

	// -------------------- REFRESH TOKEN -----------------------
	@Override
	public ApiResponse refreshToken() {
		// Extract refresh token from request header
		String refreshToken = jwtUtils.getToken(httpRequest);

		// Validate the refresh token
		if (!jwtUtils.validateRefreshToken(refreshToken)) {
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN);
		}

		// Fetch session associated with the refresh token
		Session session = sessionRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_RECOGNIZED));

		// Ensure session belongs to an admin
		if (session.getAdmin() == null) {
			throw new ForbiddenException("Refresh token does not belong to an admin");
		}

		// Fetch admin by email
		String email = jwtUtils.extractEmail(refreshToken);
		Admin admin = adminService.getAdminByEmailAndIsActive(email, true);

		// Verify the session actually belongs to this admin
		if (!session.getAdmin().getAdminId().equals(admin.getAdminId())) {
			throw new ForbiddenException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
		}

		// Generate a new access token
		String newAccessToken = jwtUtils.generateToken(email, admin.getRole().getRoleName(), TokenType.ACCESS_TOKEN,
				OtpType.LOGIN, true);

		// Update the session with the new access token
		sessionService.updateAccessTokenForRefreshToken(admin, refreshToken, newAccessToken);

		// Return the new access token in response
		return AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_GENERATED, newAccessToken);
	}

	// -------------------- HELPER METHODS -----------------------
//	private SendEmailRequest mapToSendEmailRequest(String email, String username, String otp, OtpType otpType,
//			EmailTemplateType templateType) {
//		return SendEmailRequest.builder().to(email).username(username).otp(otp).otpType(otpType)
//				.templateType(templateType)
//				.dataTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))).build();
//	}
}
