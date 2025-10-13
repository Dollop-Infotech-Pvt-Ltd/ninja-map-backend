package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateUserRequest;
import com.ninjamap.app.payload.request.UserRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.UserResponse;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IOtpService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {

	private final IUserRepository userRepository;
	private final JwtUtils jwtUtils;
	private final IOtpService otpService;
	private final IRolesRepository rolesRepository;
	private final PasswordEncoder passwordEncoder;
	private final ICloudinaryService cloudinaryService;

	@Override
	public UserResponse getCurrectUserFromToken() {
		String accessToken = jwtUtils.extractTokenFromHeader();
		String username = jwtUtils.extractEmail(accessToken);
		return mapToUserResponse(getUserByEmailAndIsActive(username, true));
	}

	@Override
	public User getUserByEmailAndIsActive(String email, Boolean isActive) {
		return userRepository.findByEmailAndOptionalIsActive(email, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
	}

	@Override
	public User getUserByIdAndIsActive(String id, Boolean isActive) {
		return userRepository.findByIdAndOptionalIsActive(id, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
	}

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(AppConstants.USER_NOT_FOUND));
		List<SimpleGrantedAuthority> authorities = user.getRole().getPermissions().stream()
				.map(p -> new SimpleGrantedAuthority(p.getResource() + "." + p.getAction()))
				.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

	@Override
	public ResponseEntity<ApiResponse> getUser(String id, Boolean isActive) {
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.USER_SUCCESSFULLY_FATCH,
				mapToUserResponse(getUserByIdAndIsActive(id, isActive))));
	}

	private UserResponse mapToUserResponse(User user) {
		if (user == null) {
			return null;
		}

		return UserResponse.builder().id(user.getUserId()).email(user.getEmail())
				.fullName(user.getFirstName() + " " + user.getLastName()).mobileNumber(user.getMobileNumber())
				.profilePicture(user.getProfilePicture()).isActive(user.getIsActive()).bio(user.getBio())
				.joiningDate(user.getCreatedDate()).role(user.getRole().getRoleName()).build();
	}

	@Override
	public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, User.class);
		Page<User> adminPage = userRepository.findAllByFilters(paginationRequest.getSearchValue(), pageable);

		List<UserResponse> responses = adminPage.stream().map(this::mapToUserResponse).toList();
		return ResponseEntity
				.ok(new PaginatedResponse<>(new PageImpl<>(responses, pageable, adminPage.getTotalElements())));
	}

	@Override
	public ResponseEntity<ApiResponse> upateUser(UpdateUserRequest userRequest) {
		User existsUser = userRepository.findByUserIdAndIsDeletedFalse(userRequest.getId())
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

		// Duplicate email check
		if (userRequest.getEmail() != null && !userRequest.getEmail().equals(existsUser.getEmail())) {
			if (userRepository.existsByEmailAndIsDeletedFalse(userRequest.getEmail())) {
				throw new ResourceAlreadyExistException(AppConstants.EMAIL_ALREADY_REGISTERED);
			}
			existsUser.setEmail(userRequest.getEmail());
		}

		// Duplicate mobile number check
		if (userRequest.getMobileNumber() != null
				&& !userRequest.getMobileNumber().equals(existsUser.getMobileNumber())) {
			if (userRepository.existsByMobileNumberAndIsDeletedFalse(userRequest.getMobileNumber())) {
				throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);
			}
			existsUser.setMobileNumber(userRequest.getMobileNumber());
		}

		existsUser.setFirstName(
				userRequest.getFirstName() != null ? userRequest.getFirstName() : existsUser.getFirstName());
		existsUser
				.setLastName(userRequest.getLastName() != null ? userRequest.getLastName() : existsUser.getLastName());
		// Update bio if provided
		if (userRequest.getBio() != null) {
			existsUser.setBio(userRequest.getBio());
		}
		// Upload profile picture if provided
		Optional.ofNullable(userRequest.getProfilePicture()).filter(file -> !file.isEmpty()).ifPresent(
				file -> existsUser.setProfilePicture(cloudinaryService.uploadFile(file, AppConstants.PROFILE_PICTURE)));

		User updatedUser = userRepository.save(existsUser);

		ApiResponse response = (updatedUser != null)
				? AppUtils.buildSuccessResponse(AppConstants.USER_PROFILE_SUCCESSFULLY_UPDATED)
				: AppUtils.buildFailureResponse(AppConstants.USER_PROFILE_NOT_UPDATED);
		return new ResponseEntity<ApiResponse>(response, response.getHttp());

	}

	@Override
	public ResponseEntity<ApiResponse> deleteUser(String id) {
		User user = getUserByIdAndIsActive(id, null);

		user.setIsDeleted(true);
		user.setIsActive(false);

		User deletedUser = userRepository.save(user);

		ApiResponse response = deletedUser != null
				? AppUtils.buildSuccessResponse(AppConstants.USER_DELETED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.USER_NOT_DELETED);

		return new ResponseEntity<ApiResponse>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> updateIsActiveStatus(String id, Boolean isActive) {
		User user = getUserByIdAndIsActive(id, null);

		if (user.getIsActive() != null && user.getIsActive().equals(isActive)) {
			String message = isActive ? AppConstants.USER_ALREADY_ACTIVE : AppConstants.USER_ALREADY_INACTIVE;
			ApiResponse failureResponse = AppUtils.buildFailureResponse(message);
			return new ResponseEntity<ApiResponse>(failureResponse, failureResponse.getHttp());
		}

		user.setIsActive(isActive);

		User updatedUser = userRepository.save(user);

		ApiResponse response = updatedUser != null
				? AppUtils.buildSuccessResponse(AppConstants.USER_STATUS_UPDATED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.USER_STATUS_NOT_UPDATED);

		return new ResponseEntity<ApiResponse>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> sendDeleteOtp(String mobileNumber) {
		User user = getUserByMobileNumberAndIsActive(mobileNumber, true);
		System.err.println("TOKEN ID => " + getCurrectUserFromToken().getId());
		System.err.println("USER ID => " + user.getUserId());
		System.err.println("EQUALS OR NOT => " + user.getUserId().equals(getCurrectUserFromToken().getId()));

		// Ensure the logged-in user is the owner of this mobile number
		if (!user.getUserId().equals(getCurrectUserFromToken().getId()))
			throw new ForbiddenException(AppConstants.UNAUTHORIZED_DELETE);

		String otp = otpService.generateOtp(user.getEmail(), OtpType.DELETE_ACCOUNT);

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.OTP_SENT, otp));
	}

	@Override
	public ResponseEntity<ApiResponse> resendDeleteOtp() {
		UserResponse currectUser = getCurrectUserFromToken();

		User user = getUserByIdAndIsActive(currectUser.getId(), true);

		String otp = otpService.generateOtp(user.getEmail(), OtpType.DELETE_ACCOUNT);

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.OTP_SENT, otp));
	}

	@Override
	public ResponseEntity<ApiResponse> verifyOtpAndDelete(String otp) {
		UserResponse currectUser = getCurrectUserFromToken();

		User user = getUserByIdAndIsActive(currectUser.getId(), true);

		boolean isVerified = otpService.validateOtp(user.getEmail(), otp, OtpType.DELETE_ACCOUNT);

		if (!isVerified) {
			throw new BadRequestException(AppConstants.INVALID_OR_EXPIRED_OTP);
		}

		// Soft delete
		user.setIsDeleted(true);
		user.setIsActive(false);
		userRepository.save(user);

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.USER_ACCOUNT_DELETED_SUCCESSFULLY));
	}

	private User getUserByMobileNumberAndIsActive(String mobileNumber, Boolean isActive) {
		return userRepository.findByMobileNumerAndOptionalIsActive(mobileNumber, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
	}

	@Override
	public ResponseEntity<ApiResponse> createUser(UserRequest request) {

		// Check for duplicate email or mobile
		User existingUser = userRepository
				.findByEmailOrMobileNumberAndIsDeletedFalse(request.getEmail(), request.getMobileNumber()).orElse(null);

//		System.err.println("===> " + existingUser.getIsActive() + ", ISDELETED => " + existingUser.getIsDeleted());
		if (existingUser != null) {
			if (existingUser.getEmail().equals(request.getEmail())) {
				throw new ResourceAlreadyExistException(AppConstants.EMAIL_ALREADY_REGISTERED);
			}
			if (existingUser.getMobileNumber().equals(request.getMobileNumber())) {
				throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);
			}
		}

		// Fetch default USER role (optional: you may pass roleId in request)
		Roles userRole = rolesRepository.findByRoleNameAndIsActiveAndIsDeleted("USER", true, false)
				.orElseThrow(() -> new ResourceNotFoundException("USER " + AppConstants.ROLE_NOT_FOUND));

		// Build User entity
		User user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
				.email(request.getEmail()).mobileNumber(request.getMobileNumber())
				.password(passwordEncoder.encode(request.getPassword())).role(userRole).bio(request.getBio()).build();

		// Upload profile picture if provided
		Optional.ofNullable(request.getProfilePicture()).filter(file -> !file.isEmpty()).ifPresent(
				file -> user.setProfilePicture(cloudinaryService.uploadFile(file, AppConstants.PROFILE_PICTURE)));

		// Save user
		User saved = userRepository.save(user);

		// Build response based on save result
		ApiResponse response = (saved != null) ? AppUtils.buildCreatedResponse(AppConstants.USER_CREATED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.USER_NOT_CREATED);

		// Return ResponseEntity with proper HTTP status
		return new ResponseEntity<>(response, response.getHttp());
	}

}
