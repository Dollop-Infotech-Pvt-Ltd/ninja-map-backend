//package com.ninjamap.app.service.impl;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.ninjamap.app.enums.OtpType;
//import com.ninjamap.app.enums.RoleType;
//import com.ninjamap.app.enums.TokenType;
//import com.ninjamap.app.exception.BadRequestException;
//import com.ninjamap.app.exception.ForbiddenException;
//import com.ninjamap.app.exception.ResourceAlreadyExistException;
//import com.ninjamap.app.exception.ResourceNotFoundException;
//import com.ninjamap.app.exception.UnauthorizedException;
//import com.ninjamap.app.model.Admin;
//import com.ninjamap.app.model.Session;
//import com.ninjamap.app.model.TempUser;
//import com.ninjamap.app.model.User;
//import com.ninjamap.app.payload.request.ForgetPasswordRequest;
//import com.ninjamap.app.payload.request.LoginRequest;
//import com.ninjamap.app.payload.request.OtpRequest;
//import com.ninjamap.app.payload.request.RegisterRequest;
//import com.ninjamap.app.payload.request.ResetPasswordRequest;
//import com.ninjamap.app.payload.response.ApiResponse;
//import com.ninjamap.app.repository.IRolesRepository;
//import com.ninjamap.app.repository.ISessionRepository;
//import com.ninjamap.app.repository.ITempUserRepository;
//import com.ninjamap.app.repository.IUserRepository;
//import com.ninjamap.app.service.IAdminService;
//import com.ninjamap.app.service.IAuthenticationService;
//import com.ninjamap.app.service.IOtpService;
//import com.ninjamap.app.service.ISessionService;
//import com.ninjamap.app.service.IUserService;
//import com.ninjamap.app.utils.AppUtils;
//import com.ninjamap.app.utils.DeviceMetadataUtil;
//import com.ninjamap.app.utils.JwtUtils;
//import com.ninjamap.app.utils.constants.AppConstants;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Service
//public class AuthenticationServiceImpl implements IAuthenticationService {
//
//	private final IUserService userService;
//	private final IRolesRepository rolesRepository;
//	private final ITempUserRepository tempUserRepository;
//	private final PasswordEncoder passwordEncoder;
//	private final JwtUtils jwtUtils;
//	private final IOtpService otpService;
//	private final IAdminService adminService;
//	private final DeviceMetadataUtil deviceMetadataUtil;
//	private final HttpServletRequest request;
//	private final ISessionService sessionService;
//	private final ISessionRepository sessionRepository;
//	private final IUserRepository userRepository;
//	private final EmailService emailService;
//
//	// Constructor Injection
//	public AuthenticationServiceImpl(IUserService userService, IRolesRepository rolesRepository,
//			ITempUserRepository tempUserRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
//			IOtpService otpService, IAdminService adminService, DeviceMetadataUtil deviceMetadataUtil,
//			HttpServletRequest request, ISessionService sessionService, ISessionRepository sessionRepository,
//			IUserRepository userRepository, EmailService emailService) {
//		this.userService = userService;
//		this.rolesRepository = rolesRepository;
//		this.tempUserRepository = tempUserRepository;
//		this.passwordEncoder = passwordEncoder;
//		this.jwtUtils = jwtUtils;
//		this.otpService = otpService;
//		this.adminService = adminService;
//		this.deviceMetadataUtil = deviceMetadataUtil;
//		this.request = request;
//		this.sessionService = sessionService;
//		this.sessionRepository = sessionRepository;
//		this.userRepository = userRepository;
//		this.emailService = emailService;
//	}
//
//	@Override
//	public void csrfTokenGeneration() {
//		jwtUtils.generateCsrf();
//
//	}
//
//	@Override
//	public ApiResponse register(RegisterRequest request) {
//		if (userRepository.findByEmailAndOptionalIsActive(request.getEmail(), true).isPresent()) {
//			throw new ResourceAlreadyExistException(AppConstants.EMAIL_ALREADY_REGISTERED);
//		}
//
//		TempUser tempUser = tempUserRepository.findByEmail(request.getEmail()).orElse(TempUser.builder().build());
//		tempUser.setEmail(request.getEmail());
//		tempUser.setPassword(passwordEncoder.encode(request.getPassword()));
//		tempUser.setFirstName(request.getFirstName());
//		tempUser.setLastName(request.getLastName());
//		tempUser.setMobileNumber(request.getMobileNumber());
//		tempUser.setRole(rolesRepository.findByRoleNameAndIsActiveAndIsDeleted(RoleType.USER, true, false)
//				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND)));
//		tempUserRepository.save(tempUser);
//
//		String token = jwtUtils.generateToken(tempUser.getEmail(), RoleType.USER, TokenType.AUTH_TOKEN,
//				OtpType.REGISTER, false);
//		String otp = otpService.generateOtp(request.getEmail(), OtpType.REGISTER);
////		emailService.sendOtpEmail(request.getEmail(), request.getFirstName()+" "+request.getLastName(), otp, OtpType.REGISTER);
//
//		Map<String, Object> res = new HashMap<>();
//		res.put(AppConstants.AUTH_TOKEN, token);
//		res.put(AppConstants.OTP, otp);
//
//		return AppUtils.buildSuccessResponse(AppConstants.OTP_SENT_FOR_LOGIN, res);
//	}
//
//	@Override
//	public ApiResponse login(LoginRequest request) {
//		String email = request.getUsername();
//		String password = request.getPassword();
//
//		try {
//			// Try User first
//			User user = userService.getUserByEmailAndIsActive(email, true);
//			String fullName = user.getFirstName() + " " + user.getLastName();
//			return buildLoginResponse(user.getEmail(), user.getPassword(), user.getRole().getRoleName(), password,
//					fullName);
//
//		} catch (ResourceNotFoundException e) {
//			try {
//				// If not found, try Admin
//				Admin admin = adminService.getAdminByEmailAndIsActive(email, true);
//				String fullName = admin.getFirstName() + " " + admin.getLastName();
//				return buildLoginResponse(admin.getEmail(), admin.getPassword(), admin.getRole().getRoleName(),
//						password, fullName);
//			} catch (ResourceNotFoundException ex) {
//				// Neither user nor admin found → throw custom error
//				throw new ResourceNotFoundException("No active user/admin account found for email: " + email);
//			}
//		}
//	}
//
//	@Override
//	public ApiResponse verifyOtp(OtpRequest otpRequest) {
//		String jwtToken = jwtUtils.getToken(request);
//
//		// Validate token & OTP type
//		validateAuthToken(jwtToken, Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD, OtpType.REGISTER));
//
//		String email = jwtUtils.extractEmail(jwtToken);
//		OtpType otpType = jwtUtils.extractOtpType(jwtToken);
//
//		// Validate OTP
//		if (!otpService.validateOtp(email, otpRequest.getOtp(), otpType)) {
//			throw new BadRequestException(AppConstants.INVALID_OR_EXPIRED_OTP);
//		}
//
//		Object account = null; // Can be User or Admin
//
//		// Try fetching User first
//		try {
//			account = userService.getUserByEmailAndIsActive(email, true);
//		} catch (ResourceNotFoundException e) {
//			// If not found, try Admin
//			try {
//				account = adminService.getAdminByEmailAndIsActive(email, true);
//			} catch (ResourceNotFoundException ex) {
//				// For REGISTER OTP, promote temp user if User not found
//				if (otpType == OtpType.REGISTER) {
//					account = promoteTempUser(email);
//				}
//			}
//		}
//
//		if (account == null) {
//			throw new ResourceNotFoundException(AppConstants.USER_NOT_FOUND);
//		}
//
//		RoleType role;
//		if (account instanceof User) {
//			role = ((User) account).getRole().getRoleName();
//		} else if (account instanceof Admin) {
//			role = ((Admin) account).getRole().getRoleName();
//		} else {
//			throw new IllegalStateException("Unknown account type");
//		}
//
//		Map<String, Object> res = resolveTokenType(otpType) == TokenType.AUTH_TOKEN
//				? Map.of(AppConstants.AUTH_TOKEN,
//						jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, otpType, true))
//				: generateOtpAndToken(account, otpType);
//
//		return AppUtils.buildSuccessResponse(AppConstants.OTP_VERIFIED_SUCCESSFULLY, res);
//	}
//
//	@Override
//	public ApiResponse resendOtp() {
//		String oldToken = jwtUtils.getToken(request);
//		String email = jwtUtils.extractEmail(oldToken);
//		OtpType otpType = jwtUtils.extractOtpType(oldToken);
//		Boolean isOtpVerified = jwtUtils.extractIsOtpVerified(oldToken);
//
//		if (Boolean.TRUE.equals(isOtpVerified)) {
//			throw new BadRequestException(AppConstants.OTP_ALREADY_VERIFIED);
//		}
//
//		if (!Set.of(OtpType.LOGIN, OtpType.FORGET_PASSWORD).contains(otpType)) {
//			throw new BadRequestException(AppConstants.RESEND_OTP_NOT_ALLOWED_FOR_THIS_TYPE);
//		}
//
//		// Generate new OTP
//		String otp = otpService.generateOtp(email, otpType);
//
//		// Find the active account: User or Admin
//		Object account;
//		String fullName = "";
//		try {
//			account = userService.getUserByEmailAndIsActive(email, true);
//			fullName = ((User) account).getFirstName() + " " + ((User) account).getLastName();
//		} catch (ResourceNotFoundException e) {
//			try {
//				account = adminService.getAdminByEmailAndIsActive(email, true);
//				fullName = ((Admin) account).getFirstName() + " " + ((Admin) account).getLastName();
//			} catch (ResourceNotFoundException ex) {
//				// Neither user nor admin found → throw custom error
//				throw new ResourceNotFoundException("No active user/admin account found for email: " + email);
//			}
//		}
////		emailService.sendOtpEmail(email, fullName, otp, otpType);
//
//		// Extract role
//		RoleType role = account instanceof User user ? user.getRole().getRoleName()
//				: ((Admin) account).getRole().getRoleName();
//
//		// Generate new token
//		String token = jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, otpType, false);
//
//		Map<String, Object> data = new HashMap<>();
//		data.put(AppConstants.AUTH_TOKEN, token);
//		data.put(AppConstants.OTP, otp);
//
//		return AppUtils.buildSuccessResponse(AppConstants.OTP_SENT, data);
//	}
//
//	@Override
//	public ApiResponse forgotPassword(ForgetPasswordRequest request) {
//		String email = request.getUsername();
//
//		Object account;
//		String fullName;
//		try {
//			account = userService.getUserByEmailAndIsActive(email, true);
//			fullName = ((User) account).getFirstName() + " " + ((User) account).getLastName();
//		} catch (ResourceNotFoundException e) {
//			try {
//				account = adminService.getAdminByEmailAndIsActive(email, true);
//				fullName = ((Admin) account).getFirstName() + " " + ((Admin) account).getLastName();
//			} catch (ResourceNotFoundException ex) {
//				// Neither user nor admin found → throw custom error
//				throw new ResourceNotFoundException("No active user/admin account found for email: " + email);
//			}
//		}
//
//		RoleType role = account instanceof User user ? user.getRole().getRoleName()
//				: ((Admin) account).getRole().getRoleName();
//
//		String token = jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, OtpType.FORGET_PASSWORD, false);
//		String otp = otpService.generateOtp(email, OtpType.FORGET_PASSWORD);
////		emailService.sendOtpEmail(email, fullName, otp, OtpType.FORGET_PASSWORD);
//
//		Map<String, Object> res = new HashMap<>();
//		res.put(AppConstants.AUTH_TOKEN, token);
//		res.put(AppConstants.OTP, otp);
//
//		return AppUtils.buildSuccessResponse(AppConstants.OTP_SENT_FOR_RESET_PASSWORD, res);
//	}
//
//	@Override
//	public ApiResponse resetPassword(ResetPasswordRequest requestPasswordRequest) {
//		String token = jwtUtils.getToken(request);
//		validateAuthToken(token, Set.of(OtpType.FORGET_PASSWORD));
//
//		Boolean isOtpVerified = jwtUtils.extractIsOtpVerified(token);
//		if (!Boolean.TRUE.equals(isOtpVerified)) {
//			throw new UnauthorizedException(AppConstants.OTP_NOT_VERIFIED);
//		}
//
//		String email = jwtUtils.extractEmail(token);
//
//		Object account;
//		try {
//			account = userService.getUserByEmailAndIsActive(email, true);
//		} catch (ResourceNotFoundException e) {
//			try {
//				account = adminService.getAdminByEmailAndIsActive(email, true);
//			} catch (ResourceNotFoundException ex) {
//				// Neither user nor admin found → throw custom error
//				throw new ResourceNotFoundException("No active user/admin account found for email: " + email);
//			}
//		}
//
//		String currentPassword = account instanceof User user ? user.getPassword() : ((Admin) account).getPassword();
//		if (passwordEncoder.matches(requestPasswordRequest.getNewPassword(), currentPassword)) {
//			throw new BadRequestException(AppConstants.PASSWORD_SHOULD_BE_DIFFERENT);
//		}
//
//		if (account instanceof User user) {
//			user.setPassword(passwordEncoder.encode(requestPasswordRequest.getNewPassword()));
//			userService.saveUser(user);
//		} else {
//			Admin admin = (Admin) account;
//			admin.setPassword(passwordEncoder.encode(requestPasswordRequest.getNewPassword()));
//			adminService.saveAdmin(admin);
//		}
//
//		return AppUtils.buildSuccessResponse(AppConstants.PASSWORD_RESET_SUCCESS);
//	}
//
//	@Override
//	public ApiResponse refreshToken() {
//		String refreshToken = jwtUtils.getToken(request);
//
//		if (!jwtUtils.validateRefreshToken(refreshToken)) {
//			throw new UnauthorizedException(AppConstants.INVALID_TOKEN);
//		}
//
//		Session session = sessionRepository.findByRefreshToken(refreshToken)
//				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_RECOGNIZED));
//
//		String email = jwtUtils.extractEmail(refreshToken);
//
//		Object account;
//		try {
//			account = userService.getUserByEmailAndIsActive(email, true);
//		} catch (ResourceNotFoundException e) {
//			try {
//				account = adminService.getAdminByEmailAndIsActive(email, true);
//			} catch (ResourceNotFoundException ex) {
//				// Neither user nor admin found → throw custom error
//				throw new ResourceNotFoundException("No active user/admin account found for email: " + email);
//			}
//		}
//
//		String accountId = account instanceof User user ? user.getUserId() : ((Admin) account).getAdminId();
//		RoleType role = account instanceof User user ? user.getRole().getRoleName()
//				: ((Admin) account).getRole().getRoleName();
//
//		if (!session.getUserId().equals(accountId)) {
//			throw new ForbiddenException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
//		}
//
//		String newAccessToken = jwtUtils.generateToken(email, role, TokenType.ACCESS_TOKEN, OtpType.LOGIN, true);
//		sessionService.updateAccessTokenForRefreshToken(account, refreshToken, newAccessToken);
//
//		return AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_GENERATED, newAccessToken);
//	}
//
//	// ........................ HELPER METHOD'S ................................
//
//	private ApiResponse buildLoginResponse(String email, String encodedPassword, RoleType role, String password,
//			String fullName) {
//		if (!passwordEncoder.matches(password, encodedPassword)) {
//			throw new UnauthorizedException(AppConstants.INVALID_CREDENTIALS);
//		}
//
//		// Generate token & OTP
//		String token = jwtUtils.generateToken(email, role, TokenType.AUTH_TOKEN, OtpType.LOGIN, false);
//		String otp = otpService.generateOtp(email, OtpType.LOGIN);
//
////		emailService.sendOtpEmail(email, fullName, otp, OtpType.LOGIN);
//		Map<String, Object> res = new HashMap<>();
//		res.put(AppConstants.AUTH_TOKEN, token);
//		res.put(AppConstants.OTP, otp);
//
//		return AppUtils.buildSuccessResponse(AppConstants.OTP_SENT_FOR_LOGIN, res);
//	}
//
//	private TokenType resolveTokenType(OtpType otpType) {
//		return otpType == OtpType.FORGET_PASSWORD ? TokenType.AUTH_TOKEN : TokenType.ACCESS_TOKEN;
//	}
//
//	private void validateAuthToken(String token, Set<OtpType> allowedOtpTypes) {
//		if (token == null)
//			throw new UnauthorizedException(AppConstants.MISSING_TOKEN);
//		if (jwtUtils.isTokenExpired(token))
//			throw new UnauthorizedException(AppConstants.TOKEN_EXPIRED);
//
//		TokenType tokenType = jwtUtils.extractTokenType(token);
//		OtpType otpType = jwtUtils.extractOtpType(token);
//
//		if (tokenType != TokenType.AUTH_TOKEN || !allowedOtpTypes.contains(otpType)) {
//			throw new UnauthorizedException(AppConstants.INVALID_TOKEN_TYPE_FOR_VERIFICATION);
//		}
//	}
//
//	/**
//	 * Promote TempUser to permanent User
//	 */
//	private User promoteTempUser(String email) {
//		TempUser tempUser = tempUserRepository.findByEmail(email)
//				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
//
//		User user = User.builder().email(tempUser.getEmail()).password(tempUser.getPassword())
//				.firstName(tempUser.getFirstName()).lastName(tempUser.getLastName())
//				.mobileNumber(tempUser.getMobileNumber()).role(tempUser.getRole()).build();
//
//		user = userService.saveUser(user);
//		tempUserRepository.delete(tempUser);
//		return user;
//	}
//
//	/**
//	 * Generate OTP and Tokens for both User and Admin
//	 */
//	private Map<String, Object> generateOtpAndToken(Object account, OtpType otpType) {
//		String email;
//		RoleType role;
//
//		if (account instanceof User user) {
//			email = user.getEmail();
//			role = user.getRole().getRoleName();
//		} else if (account instanceof Admin admin) {
//			email = admin.getEmail();
//			role = admin.getRole().getRoleName();
//		} else {
//			throw new IllegalStateException("Unknown account type");
//		}
//
//		String token = jwtUtils.generateToken(email, role, resolveTokenType(otpType), otpType, true);
//		String refreshToken = jwtUtils.generateRefreshToken(email, role);
//
//		String ipAddress = deviceMetadataUtil.getClientIp(this.request);
//		String userAgent = this.request.getHeader(AppConstants.USER_AGENT);
////		sessionService.createSession(account, token, refreshToken, userAgent, ipAddress);
//
//		Map<String, Object> res = new HashMap<>();
//		res.put(AppConstants.ACCESS_TOKEN, token);
//		res.put(AppConstants.REFRESH_TOKEN, refreshToken);
//		return res;
//	}
//
//}
