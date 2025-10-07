package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateUserRequest;
import com.ninjamap.app.payload.request.UserRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.UserResponse;

public interface IUserService {

	public UserResponse getCurrectUserFromToken();

	public User getUserByEmailAndIsActive(String email, Boolean isActive);

	public User saveUser(User user);

	public User getUserByIdAndIsActive(String id, Boolean isActive);

	public ResponseEntity<ApiResponse> getUser(String id, Boolean isActive);

	public ResponseEntity<PaginatedResponse<UserResponse>> getAllUsers(PaginationRequest paginationRequest);

	public ResponseEntity<ApiResponse> upateUser(UpdateUserRequest userRequest);

	public ResponseEntity<ApiResponse> deleteUser(String id);

	public ResponseEntity<ApiResponse> updateIsActiveStatus(String id, Boolean isActive);

	public ResponseEntity<ApiResponse> sendDeleteOtp(String mobileNumber);

	public ResponseEntity<ApiResponse> resendDeleteOtp();

	public ResponseEntity<ApiResponse> verifyOtpAndDelete(String otp);

	public ResponseEntity<ApiResponse> createUser(UserRequest request);
}
