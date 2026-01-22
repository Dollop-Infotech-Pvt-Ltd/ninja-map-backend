package com.ninjamap.app.service.impl;

import java.util.Map;
import java.util.Optional;
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
import com.ninjamap.app.model.PersonalInfo;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.model.TempUser;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.AppRegisterRequest;
import com.ninjamap.app.payload.request.ChangePasswordRequest;
import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.MobileLoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.RegisterRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.repository.ITempUserRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.service.IUserAuthService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.AuthServiceHelper;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.PasswordGenerator;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
	private final ISessionService sessionService;
	private final ISessionRepository sessionRepository;
	private final HttpServletRequest httpRequest;
	private final AuthServiceHelper authHelper;
	private final ICloudinaryService cloudinaryService;

	// -------------------- LOGIN -----------------------
	@Override
	public ApiResponse login(LoginRequest request) {
		User user = userService.getUserByEmailAndIsActive(request.getUsername(), null);
		if (Boolean.FALSE.equals(user.getIsActive())) {
			throw new ResourceNotFoundException(AppConstants.USER_ACCOUNT_INACTIVE);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPersonalInfo().getPassword())) {
			throw new UnauthorizedException(AppConstants.INVALID_CREDENTIALS);
		}

		return authHelper.generateOtpAndAuthToken(user.getPersonalInfo().getEmail(), user.getRole().getRoleName(),
				OtpType.LOGIN, EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- OTP VERIFICATION -----------------------
	@Override
	public ApiResponse verifyOtp(OtpRequest otpRequest) {
		var context = authHelper.validateAndExtractOtp(otpRequest,
				Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD, OtpType.REGISTER, OtpType.MOBILE_VERIFICATION));

		String email = context.email();
		OtpType otpType = context.otpType();

		if (OtpType.MOBILE_VERIFICATION.equals(otpType)) {
			String authToken = jwtUtils.generateToken(email, "USER", TokenType.AUTH_TOKEN, otpType, true);
			return AppUtils.buildCreatedResponse(AppConstants.MOBILE_NOT_REGISTERED,
					Map.of(AppConstants.AUTH_TOKEN, authToken));

		}

		User user = getOrPromoteUser(email, otpType);

		if (authHelper.isAuthTokenRequired(otpType)) {
			return authHelper.buildAuthTokenResponse(email, user.getRole().getRoleName(), otpType);
		} else {
			ApiResponse response = authHelper.generateAccessAndRefreshTokensAndSession(
					user.getPersonalInfo().getEmail(), user.getRole().getRoleName(), otpType, user);

//          if (otpType == OtpType.LOGIN) {
//              sendEmail(user.getPersonalInfo().getEmail(),
//                        user.getPersonalInfo().getFirstName() + " " + user.getPersonalInfo().getLastName(),
//                        null, OtpType.LOGIN, EmailTemplateType.LOGIN_SUCCESS_NOTIFICATION);
//          }

			@SuppressWarnings("unchecked")
			Map<String, Object> tokens = (Map<String, Object>) response.getData();
			tokens.put("user", userService.mapToUserResponse(user));
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
		if (!Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD, OtpType.REGISTER, OtpType.MOBILE_VERIFICATION)
				.contains(otpType))
			throw new BadRequestException(AppConstants.RESEND_OTP_NOT_ALLOWED_FOR_THIS_TYPE);

		UserOrTemp entity = fetchUserOrTempUser(email, otpType);
		return authHelper.generateOtpAndAuthToken(entity.email, entity.role, otpType,
				EmailTemplateType.OTP_VERIFICATION);
	}

	// -------------------- FORGOT PASSWORD -----------------------
	@Override
	public ApiResponse forgotPassword(ForgetPasswordRequest request) {
		User user = userService.getUserByEmailAndIsActive(request.getUsername(), true);
		return authHelper.generateOtpAndAuthToken(user.getPersonalInfo().getEmail(), user.getRole().getRoleName(),
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
		User user = userService.getUserByEmailAndIsActive(email, true);

		if (passwordEncoder.matches(request.getNewPassword(), user.getPersonalInfo().getPassword())) {
			throw new BadRequestException(AppConstants.PASSWORD_SHOULD_BE_DIFFERENT);
		}

		user.getPersonalInfo().setPassword(passwordEncoder.encode(request.getNewPassword()));
		userService.saveUser(user);

//      sendEmail(user.getPersonalInfo().getEmail(),
//                user.getPersonalInfo().getFirstName() + " " + user.getPersonalInfo().getLastName(),
//                null, null, EmailTemplateType.PASSWORD_UPDATE_NOTIFICATION);

		return AppUtils.buildSuccessResponse(AppConstants.PASSWORD_RESET_SUCCESS);
	}

	// -------------------- REFRESH TOKEN -----------------------
	@Override
	public ApiResponse refreshToken() {
		String refreshToken = jwtUtils.getToken(httpRequest);

		if (!jwtUtils.validateRefreshToken(refreshToken)) {
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN);
		}

		Session session = sessionRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_RECOGNIZED));

		if (session.getUser() == null) {
			throw new ForbiddenException("Refresh token does not belong to a user");
		}

		User user = userService.getUserByEmailAndIsActive(jwtUtils.extractEmail(refreshToken), true);

		if (!session.getUser().getUserId().equals(user.getUserId())) {
			throw new ForbiddenException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
		}

		String newAccessToken = jwtUtils.generateToken(user.getPersonalInfo().getEmail(), user.getRole().getRoleName(),
				TokenType.ACCESS_TOKEN, OtpType.LOGIN, true);

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

		// Fetch existing TempUser or create a new one
		TempUser tempUser = tempUserRepository.findByPersonalInfo_Email(request.getEmail())
				.orElse(TempUser.builder().build());

		// Build or overwrite PersonalInfo using builder to avoid nulls
		PersonalInfo personalInfo = PersonalInfo.builder().email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).firstName(request.getFirstName())
				.lastName(request.getLastName()).mobileNumber(request.getMobileNumber()).build();

		tempUser.setPersonalInfo(personalInfo);
		tempUser.setRole(rolesRepository.findByRoleNameAndIsActiveAndIsDeleted("USER", true, false)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND)));

		tempUserRepository.save(tempUser);

		return authHelper.generateOtpAndAuthToken(tempUser.getPersonalInfo().getEmail(), "USER", OtpType.REGISTER,
				EmailTemplateType.OTP_VERIFICATION);
	}

	@Override
	public ApiResponse loginWithMobile(MobileLoginRequest request) {
		String mobileNumber = request.getMobileNumber();
		Optional<User> userOpt = userRepository.findByMobileNumberAndOptionalIsActive(mobileNumber, null);

		if (userOpt.isPresent()) {
			// Existing user → login OTP flow
			User user = userOpt.get();

			if (Boolean.FALSE.equals(user.getIsActive())) {
				throw new ResourceNotFoundException(AppConstants.USER_ACCOUNT_INACTIVE);
			}

			return authHelper.generateOtpAndAuthToken(user.getPersonalInfo().getEmail(), user.getRole().getRoleName(),
					OtpType.LOGIN, EmailTemplateType.OTP_VERIFICATION);
		}

		// Check if temp user (unregistered mobile) already exists
		Optional<TempUser> tempUserOpt = tempUserRepository.findByPersonalInfo_MobileNumber(mobileNumber);
		TempUser tempUser;

		if (tempUserOpt.isPresent()) {
			tempUser = tempUserOpt.get();
		} else {
			// Create new temporary user for mobile registration
			tempUser = new TempUser();

			PersonalInfo personalInfo = new PersonalInfo();
			personalInfo.setMobileNumber(mobileNumber);
			tempUser.setPersonalInfo(personalInfo);

			tempUser.setRole(rolesRepository.findByRoleNameAndIsActiveAndIsDeleted("USER", true, false)
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND)));

			tempUserRepository.save(tempUser);
		}

		return authHelper.generateOtpAndAuthToken(tempUser.getPersonalInfo().getMobileNumber(),
				tempUser.getRole().getRoleName(), OtpType.MOBILE_VERIFICATION, EmailTemplateType.OTP_VERIFICATION);
	}

	@Override
	public ApiResponse registerFromApp(AppRegisterRequest request) {
		String token = jwtUtils.getToken(httpRequest);
		// Check if OTP verification is true
		Boolean isOtpVerified = jwtUtils.extractIsOtpVerified(token);
		if (isOtpVerified == null || !isOtpVerified) {
			throw new UnauthorizedException(AppConstants.OTP_NOT_VERIFIED);
		}

		TempUser tempUser = tempUserRepository.findByEmailOrMobile(request.getMobileNumber())
				.filter(tu -> request.getMobileNumber().equals(tu.getPersonalInfo().getMobileNumber()))
				.orElseThrow(() -> new UnauthorizedException(AppConstants.USER_NOT_VERIFIED_OR_MISMATCH));

		if (userRepository.findByEmailAndOptionalIsActive(request.getEmail(), true).isPresent()) {
			throw new ResourceAlreadyExistException(AppConstants.EMAIL_ALREADY_REGISTERED);
		}
		if (userRepository.findByMobileNumberAndOptionalIsActive(request.getMobileNumber(), true).isPresent()) {
			throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);
		}

		Roles role = rolesRepository.findByRoleNameAndIsActiveAndIsDeleted("USER", true, false)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND));

		String encodedPassword = passwordEncoder.encode(PasswordGenerator.generatePassword());

		String profilePictureUrl = null;
		if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
			profilePictureUrl = cloudinaryService.uploadFile(request.getProfilePicture(), AppConstants.PROFILE_PICTURE);
		}

		PersonalInfo personalInfo = PersonalInfo.builder().firstName(request.getFirstName())
				.lastName(request.getLastName()).email(request.getEmail()).mobileNumber(request.getMobileNumber())
				.gender(request.getGender()).password(encodedPassword).profilePicture(profilePictureUrl).build();

		User user = User.builder().personalInfo(personalInfo).role(role).isActive(true).isDeleted(false).build();

		user = userService.saveUser(user);
		tempUserRepository.delete(tempUser);

		ApiResponse response = authHelper.generateAccessAndRefreshTokensAndSession(user.getPersonalInfo().getEmail(),
				user.getRole().getRoleName(), OtpType.REGISTER, user);
		@SuppressWarnings("unchecked")
		Map<String, Object> responseData = (Map<String, Object>) response.getData();
		responseData.put("user", userService.mapToUserResponse(user));
		return AppUtils.buildSuccessResponse(AppConstants.USER_SUCCESSFULLY_REGISTERED, responseData);

		// Return success response with both tokens
//		return AppUtils.buildCreatedResponse(AppConstants.USER_SUCCESSFULLY_REGISTERED, responseData);
	}

	// -------------------- HELPER METHODS -----------------------

	private User getOrPromoteUser(String mobileNumber, OtpType otpType) {
		Optional<User> userOpt = userRepository.findByEmailOrMobileAndOptionalIsActive(mobileNumber, true);

		if (userOpt.isPresent()) {
			return userOpt.get();
		}

		if (otpType == OtpType.REGISTER) {
			return promoteTempUser(mobileNumber);
		}

		throw new ResourceNotFoundException(AppConstants.USER_NOT_FOUND);
	}

	private User promoteTempUser(String email) {
		TempUser tempUser = tempUserRepository.findByEmailOrMobile(email)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

		System.err.println("TEMP USER ===> " + tempUser.getPersonalInfo().getMobileNumber());

		User user = User.builder().personalInfo(tempUser.getPersonalInfo()).role(tempUser.getRole()).build();

		user = userService.saveUser(user);
		tempUserRepository.delete(tempUser);

//      sendEmail(user.getPersonalInfo().getEmail(),
//                user.getPersonalInfo().getFirstName() + " " + user.getPersonalInfo().getLastName(),
//                null, OtpType.REGISTER, EmailTemplateType.REGISTRATION);

		return user;
	}

	private record UserOrTemp(String email, String fullName, String role) {
	}

	private UserOrTemp fetchUserOrTempUser(String email, OtpType otpType) {
		if (otpType == OtpType.REGISTER || otpType == OtpType.MOBILE_VERIFICATION) {
			TempUser tempUser = tempUserRepository.findByEmailOrMobile(email)
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

			PersonalInfo info = tempUser.getPersonalInfo();

			return new UserOrTemp(info.getEmail() != null ? info.getEmail() : info.getMobileNumber(),
					info.getFullName(), tempUser.getRole().getRoleName());
		}
//		User user = userService.getUserByEmailAndIsActive(email, true);
		User user = userRepository.findByEmailOrMobileAndOptionalIsActive(email, true)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
		return new UserOrTemp(user.getPersonalInfo().getEmail(),
				user.getPersonalInfo().getFirstName() + " " + user.getPersonalInfo().getLastName(),
				user.getRole().getRoleName());

	}


}
