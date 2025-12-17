package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateUserRequest;
import com.ninjamap.app.payload.request.UserRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

	private final IUserService userService;

	// ========================= CREATE USER =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.CREATE_USERS')")
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@Valid UserRequest request) {
		return userService.createUser(request);
	}

	// ========================= GET USER BY ID =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.VIEW_USERS')")
	@GetMapping("/get")
	public ResponseEntity<?> getUser(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@RequestParam(name = AppConstants.IS_ACTIVE, required = false) Boolean isActive) {
		return userService.getUser(id, isActive);
	}

	// ========================= GET ALL USERS =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.VIEW_USERS')")
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllUsers(@RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

		PaginationRequest paginationRequest = PaginationRequest.builder().pageSize(pageSize).pageNumber(pageNumber)
				.sortDirection(sortDirection).sortKey(sortKey).searchValue(searchValue).build();

		return userService.getAllUsers(paginationRequest);
	}

	// ========================= UPDATE USER =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.EDIT_USERS')")
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@Valid UpdateUserRequest request) {
		return userService.upateUser(request);
	}

	// ========================= DELETE USER =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.DELETE_USERS')")
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteUser(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return userService.deleteUser(id);
	}

	// ========================= UPDATE USER STATUS =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.EDIT_USERS')")
	@PatchMapping("/update-status")
	public ResponseEntity<?> updateUserStatus(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@RequestParam(name = AppConstants.IS_ACTIVE) Boolean isActive) {
		return userService.updateIsActiveStatus(id, isActive);
	}

	// ========================= GET LOGGED-IN USER =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.VIEW_USERS')")
	@GetMapping("/get-loggedIn-user")
	public ResponseEntity<?> getUserById() {
		return ResponseEntity.ok(userService.getCurrectUserFromToken());
	}

	// ========================= SEND OTP FOR ACCOUNT DELETION
	// =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.VIEW_USERS')")
	@PostMapping("/delete/request-otp")
	public ResponseEntity<ApiResponse> sendDeleteOtp(@RequestParam String mobileNumber) {
		return userService.sendDeleteOtp(mobileNumber);
	}

	// ========================= RESEND OTP FOR ACCOUNT DELETION
	// =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.VIEW_USERS')")
	@PostMapping("/delete/resend-otp")
	public ResponseEntity<ApiResponse> resendDeleteOtp() {
		return userService.resendDeleteOtp();
	}

	// ========================= VERIFY OTP AND DELETE ACCOUNT
	// =========================
	@PreAuthorize("hasAuthority('USER_MANAGEMENT.DELETE_USERS')")
	@PostMapping("/delete/verify-otp")
	public ResponseEntity<ApiResponse> verifyOtpAndDelete(@RequestParam(name = AppConstants.OTP) String otp) {
		return userService.verifyOtpAndDelete(otp);
	}
}
