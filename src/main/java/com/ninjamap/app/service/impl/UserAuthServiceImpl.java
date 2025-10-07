package com.ninjamap.app.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.kafka.KafkaTopics;
import com.ninjamap.app.kafka.NotificationProducer;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.model.TempUser;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.RegisterRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.repository.ITempUserRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.IOtpService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.service.IUserAuthService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.AuthServiceHelper;
import com.ninjamap.app.utils.DeviceMetadataUtil;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements IUserAuthService {

	private final IUserService userService;
	private final IRolesRepository rolesRepository;
	private final ITempUserRepository tempUserRepository;
	private final IUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final IOtpService otpService;
	private final ISessionService sessionService;
	private final ISessionRepository sessionRepository;
	private final HttpServletRequest httpRequest;
	private final AuthServiceHelper authHelper;
	private final NotificationProducer notificationProducer;
	private final DeviceMetadataUtil deviceMetadataUtil;
	private final KafkaTopics kafkaTopics;

	// -------------------- LOGIN -----------------------
	@Override
	public ApiResponse login(LoginRequest request) {
		User user = userService.getUserByEmailAndIsActive(request.getUsername(), true);

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UnauthorizedException(AppConstants.INVALID_CREDENTIALS);
		}

		return sendOtpAndGenerateAuthToken(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
				user.getRole().getRoleName(), OtpType.LOGIN, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- OTP VERIFICATION -----------------------
	@Override
	public ApiResponse verifyOtp(OtpRequest otpRequest) {
		String jwtToken = jwtUtils.getToken(httpRequest);
		validateAuthToken(jwtToken, Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD, OtpType.REGISTER));

		String email = jwtUtils.extractEmail(jwtToken);
		OtpType otpType = jwtUtils.extractOtpType(jwtToken);

		if (!otpService.validateOtp(email, otpRequest.getOtp(), otpType)) {
			throw new BadRequestException(AppConstants.INVALID_OR_EXPIRED_OTP);
		}

		User user = getOrPromoteUser(email, otpType);

		if (authHelper.isAuthTokenRequired(otpType)) {
			String authToken = jwtUtils.generateToken(email, user.getRole().getRoleName(), TokenType.AUTH_TOKEN,
					otpType, true);
			return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY,
					Map.of(AppConstants.AUTH_TOKEN, authToken));
		} else {
			Map<String, Object> tokens = authHelper.generateAccessAndRefreshTokens(email, user.getRole().getRoleName(),
					otpType);

//			if (otpType == OtpType.LOGIN) {
//				sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), null, OtpType.LOGIN,
//						EmailTemplateType.LOGIN_SUCCESS_NOTIFICATION);
//			}

			String accessToken = (String) tokens.get(AppConstants.ACCESS_TOKEN);
			String refreshToken = (String) tokens.get(AppConstants.REFRESH_TOKEN);

			String userAgent = httpRequest.getHeader(AppConstants.USER_AGENT);
			String ipAddress = deviceMetadataUtil.getClientIp(httpRequest);

			sessionService.createSession(user, accessToken, refreshToken, userAgent, ipAddress);

			return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY, tokens);
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
		if (!Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD, OtpType.REGISTER).contains(otpType))
			throw new BadRequestException(AppConstants.RESEND_OTP_NOT_ALLOWED_FOR_THIS_TYPE);

		UserOrTemp userOrTemp = fetchUserOrTempUser(email, otpType);
		return sendOtpAndGenerateAuthToken(userOrTemp.email, userOrTemp.fullName, userOrTemp.role, otpType,
				EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- FORGOT PASSWORD -----------------------
	@Override
	public ApiResponse forgotPassword(ForgetPasswordRequest request) {
		String email = request.getUsername();
		User user = userService.getUserByEmailAndIsActive(email, true);
		return sendOtpAndGenerateAuthToken(email, user.getFirstName() + " " + user.getLastName(),
				user.getRole().getRoleName(), OtpType.FORGET_PASSWORD, EmailTemplateType.OTP_VERIFICATION);
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
		User user = userService.getUserByEmailAndIsActive(email, true);

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
			throw new BadRequestException(AppConstants.PASSWORD_SHOULD_BE_DIFFERENT);
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userService.saveUser(user);
//		sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), null, null,
//				EmailTemplateType.PASSWORD_UPDATE_NOTIFICATION);

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
		User user = userService.getUserByEmailAndIsActive(email, true);

		if (!session.getAccountId().equals(user.getUserId()))
			throw new ForbiddenException(AppConstants.TOKEN_NOT_BELONG_TO_USER);

		String newAccessToken = jwtUtils.generateToken(email, user.getRole().getRoleName(), TokenType.ACCESS_TOKEN,
				OtpType.LOGIN, true);
		sessionService.updateAccessTokenForRefreshToken(user, refreshToken, newAccessToken);

		return AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_GENERATED, newAccessToken);
	}

	// -------------------- REGISTER -----------------------
	@Override
	public ApiResponse register(RegisterRequest request) {
		if (userRepository.findByEmailAndOptionalIsActive(request.getEmail(), true).isPresent())
			throw new ResourceAlreadyExistException(AppConstants.EMAIL_ALREADY_REGISTERED);

		if (userRepository.findByMobileNumberAndOptionalIsActive(request.getMobileNumber(), true).isPresent())
			throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);

		TempUser tempUser = tempUserRepository.findByEmail(request.getEmail()).orElse(TempUser.builder().build());
		tempUser.setEmail(request.getEmail());
		tempUser.setPassword(passwordEncoder.encode(request.getPassword()));
		tempUser.setFirstName(request.getFirstName());
		tempUser.setLastName(request.getLastName());
		tempUser.setMobileNumber(request.getMobileNumber());
		tempUser.setRole(rolesRepository.findByRoleNameAndIsActiveAndIsDeleted("USER", true, false)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND)));

		tempUserRepository.save(tempUser);

		return sendOtpAndGenerateAuthToken(tempUser.getEmail(), tempUser.getFirstName() + " " + tempUser.getLastName(),
				"USER", OtpType.REGISTER, EmailTemplateType.OTP_VERIFICATION);
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

	private User getOrPromoteUser(String email, OtpType otpType) {
		try {
			return userService.getUserByEmailAndIsActive(email, true);
		} catch (ResourceNotFoundException e) {
			if (otpType == OtpType.REGISTER)
				return promoteTempUser(email);
			throw new ResourceNotFoundException(AppConstants.USER_NOT_FOUND);
		}
	}

	private User promoteTempUser(String email) {
		TempUser tempUser = tempUserRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

		User user = User.builder().email(tempUser.getEmail()).password(tempUser.getPassword())
				.firstName(tempUser.getFirstName()).lastName(tempUser.getLastName())
				.mobileNumber(tempUser.getMobileNumber()).role(tempUser.getRole()).build();

		user = userService.saveUser(user);
		tempUserRepository.delete(tempUser);

//		sendEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), null, OtpType.REGISTER,
//				EmailTemplateType.REGISTRATION);
		return user;
	}

//	private void sendEmail(String email, String username, String otp, OtpType otpType, EmailTemplateType templateType) {
//		SendEmailRequest emailRequest = SendEmailRequest.builder().to(email).username(username).otp(otp)
//				.otpType(otpType).templateType(templateType)
//				.dataTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))).build();
//		notificationProducer.sendMessage(kafkaTopics.getEmailNotificationTopic(), emailRequest, OutboxType.OTP_EMAIL);
//	}

	private ApiResponse sendOtpAndGenerateAuthToken(String email, String fullName, String role, OtpType otpType,
			EmailTemplateType templateType) {
		String token = jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, otpType, false);
		String otp = otpService.generateOtp(email, otpType);

//		sendEmail(email, fullName, otp, otpType, templateType);
		return AppUtils.buildSuccessResponse(
				templateType == EmailTemplateType.OTP_VERIFICATION ? AppConstants.OTP_SENT_FOR_LOGIN
						: AppConstants.OTP_SENT,
				Map.of(AppConstants.AUTH_TOKEN, token, AppConstants.OTP, otp));
	}

	private record UserOrTemp(String email, String fullName, String role) {
	}

	private UserOrTemp fetchUserOrTempUser(String email, OtpType otpType) {
		if (otpType == OtpType.REGISTER) {
			TempUser tempUser = tempUserRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
			return new UserOrTemp(tempUser.getEmail(), tempUser.getFirstName() + " " + tempUser.getLastName(),
					tempUser.getRole().getRoleName());
		} else {
			User user = userService.getUserByEmailAndIsActive(email, true);
			return new UserOrTemp(user.getEmail(), user.getFirstName() + " " + user.getLastName(),
					user.getRole().getRoleName());
		}
	}
}
