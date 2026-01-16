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

import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.PersonalInfo;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.payload.request.AdminRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateAdminRequest;
import com.ninjamap.app.payload.response.AdminResponse;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.IAdminRepository;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService, UserDetailsService {

	private final IAdminRepository adminRepository;
	private final IRolesRepository rolesRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtils jwtUtils;
	private final ICloudinaryService cloudinaryService;

	@Override
	public AdminResponse getCurrectAdminFromToken() {
		String accessToken = jwtUtils.extractTokenFromHeader();
		String username = jwtUtils.extractEmail(accessToken);
		return mapToResponse(getAdminByEmailAndIsActive(username, true));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse> create(AdminRequest registerRequest) {
		// Single query to check for existing email or mobile
		Admin existingAdmin = adminRepository.findByEmailOrMobileNumberAndIsDeletedFalse(registerRequest.getEmail(),
				registerRequest.getMobileNumber()).orElse(null);

		if (existingAdmin != null) {
			if (existingAdmin.getPersonalInfo().getEmail().equals(registerRequest.getEmail())) {
				throw new ResourceAlreadyExistException(AppConstants.ADMIN_ALREADY_EXISTS);
			} else {
				throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);
			}
		}

		adminRepository.findByEmployeeIdAndIsDeletedFalseAndIsActive(registerRequest.getEmployeeId(), true)
				.ifPresent(a -> {
					throw new ResourceAlreadyExistException(AppConstants.EMPLOYEE_ID_ALREADY_REGISTERED);
				});

		// Fetch ADMIN role
		Roles adminRole = rolesRepository.findByRoleIdAndIsDeletedFalse(registerRequest.getRoleId())
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_ROLE_NOT_FOUND));

		// Map DTO to entity using embedded PersonalInfo
		PersonalInfo personalInfo = PersonalInfo.builder().firstName(registerRequest.getFirstName())
				.lastName(registerRequest.getLastName()).email(registerRequest.getEmail())
				.mobileNumber(registerRequest.getMobileNumber())
				.password(passwordEncoder.encode(registerRequest.getPassword())).bio(registerRequest.getBio())
				.profilePicture(null).build();

		Admin admin = Admin.builder().personalInfo(personalInfo).role(adminRole)
				.employeeId(registerRequest.getEmployeeId()).build();

		// Upload profile picture if provided
		Optional.ofNullable(registerRequest.getProfilePicture()).filter(file -> !file.isEmpty()).ifPresent(file -> admin
				.getPersonalInfo().setProfilePicture(cloudinaryService.uploadFile(file, AppConstants.PROFILE_PICTURE)));

		System.err.println("ADMIN ==> " + admin);
		// Save new admin
		Admin saved = adminRepository.save(admin);

		// Build response based on save result
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.ADMIN_CREATED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.ADMIN_NOT_CREATED);

		// Return ResponseEntity with proper HTTP status
		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public Admin getAdminByEmailAndIsActive(String email, Boolean isActive) {
		return adminRepository.findByEmailAndOptionalIsActive(email, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_NOT_FOUND));
	}

	@Override
	public Admin getAdminByIdAndIsActive(String id, Boolean isActive) {
		return adminRepository.findByAdminIdAndOptionalIsActive(id, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_NOT_FOUND));
	}

	@Override
	public Admin saveAdmin(Admin admin) {
		return adminRepository.save(admin);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepository.findByPersonalInfoEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(AppConstants.ADMIN_NOT_FOUND));
		List<SimpleGrantedAuthority> authorities = admin.getRole().getPermissions().stream()
				.map(p -> new SimpleGrantedAuthority(p.getResource() + "." + p.getAction()))
				.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(admin.getPersonalInfo().getEmail(),
				admin.getPersonalInfo().getPassword(), authorities);
	}

	@Override
	public ResponseEntity<ApiResponse> getById(String id, Boolean isActive) {
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ADMIN_SUCCESSFULLY_FATCH,
				mapToResponse(getAdminByIdAndIsActive(id, isActive))));
	}

	@Override
	public ResponseEntity<PaginatedResponse<AdminResponse>> getAllAdmins(PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, Admin.class);
//		Page<Admin> adminPage = adminRepository.findAllByFilters(paginationRequest.getSearchValue(), pageable);
		String searchValue = paginationRequest.getSearchValue();
		if (searchValue != null && !searchValue.isBlank()) {
			searchValue = searchValue.trim();
		} else {
			searchValue = null;
		}
		Page<Admin> adminPage = adminRepository.findAllByFilters(searchValue, pageable);

		List<AdminResponse> responses = adminPage.stream().map(this::mapToResponse).toList();

		PaginatedResponse<AdminResponse> paginatedResponse = new PaginatedResponse<>(
				new PageImpl<>(responses, pageable, adminPage.getTotalElements()));

		return ResponseEntity.ok(paginatedResponse);
	}

	@Override
	public ResponseEntity<ApiResponse> update(UpdateAdminRequest updateRequest) {
		Admin admin = getAdminByIdAndIsActive(updateRequest.getId(), null);

		// --- Duplicate email check ---
		if (updateRequest.getEmail() != null
				&& !updateRequest.getEmail().equalsIgnoreCase(admin.getPersonalInfo().getEmail())) {
			if (adminRepository.existsByPersonalInfoEmailAndIsDeletedFalse(updateRequest.getEmail().trim())) {
				throw new ResourceAlreadyExistException(AppConstants.ADMIN_ALREADY_EXISTS);
			}
			admin.getPersonalInfo().setEmail(updateRequest.getEmail().trim());
		}

		// --- Duplicate mobile check ---
		if (updateRequest.getMobileNumber() != null
				&& !updateRequest.getMobileNumber().equals(admin.getPersonalInfo().getMobileNumber())) {
			if (adminRepository
					.existsByPersonalInfoMobileNumberAndIsDeletedFalse(updateRequest.getMobileNumber().trim())) {
				throw new ResourceAlreadyExistException(AppConstants.MOBILE_ALREADY_REGISTERED);
			}
			admin.getPersonalInfo().setMobileNumber(updateRequest.getMobileNumber().trim());
		}

		// --- Update basic fields ---
		admin.getPersonalInfo().setFirstName(updateRequest.getFirstName() != null ? updateRequest.getFirstName()
				: admin.getPersonalInfo().getFirstName());
		admin.getPersonalInfo().setLastName(updateRequest.getLastName() != null ? updateRequest.getLastName()
				: admin.getPersonalInfo().getLastName());

		if (updateRequest.getBio() != null) {
			admin.getPersonalInfo().setBio(updateRequest.getBio());
		}

		// --- Upload profile picture if provided ---
		Optional.ofNullable(updateRequest.getProfilePicture()).filter(file -> !file.isEmpty()).ifPresent(file -> admin
				.getPersonalInfo().setProfilePicture(cloudinaryService.uploadFile(file, AppConstants.PROFILE_PICTURE)));

		// Save directly using the same entity
		Admin savedAdmin = adminRepository.save(admin);

		ApiResponse response = (savedAdmin != null) ? AppUtils.buildSuccessResponse(AppConstants.ADMIN_PROFILE_UPDATED)
				: AppUtils.buildFailureResponse(AppConstants.ADMIN_PROFILE_NOT_UPDATED);

		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse> delete(String id) {
		Admin admin = getAdminByIdAndIsActive(id, null);

		admin.setIsDeleted(true);
		admin.setIsActive(false);
		Admin savedAdmin = adminRepository.save(admin);

		ApiResponse response = (savedAdmin != null) ? AppUtils.buildSuccessResponse(AppConstants.ADMIN_DELETED)
				: AppUtils.buildFailureResponse(AppConstants.ADMIN_PROFILE_NOT_UPDATED);
		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse> updateStatus(String id, Boolean isActive) {
		Admin admin = getAdminByIdAndIsActive(id, null);

		if (admin.getIsActive() != null && admin.getIsActive().equals(isActive)) {
			String message = isActive ? AppConstants.ADMIN_ALREADY_ACTIVE : AppConstants.ADMIN_ALREADY_INACTIVE;
			ApiResponse response = AppUtils.buildFailureResponse(message);
			return new ResponseEntity<>(response, response.getHttp());
		}

		admin.setIsActive(isActive);
		Admin savedAdmin = adminRepository.save(admin);

		ApiResponse response = (savedAdmin != null) ? AppUtils.buildSuccessResponse(AppConstants.ADMIN_STATUS_UPDATED)
				: AppUtils.buildFailureResponse(AppConstants.ADMIN_STATUS_NOT_UPDATED);
		return new ResponseEntity<>(response, response.getHttp());
	}


	// ========================= HELPER METHODS ========================= //

	private AdminResponse mapToResponse(Admin admin) {
		if (admin == null) {
			return null;
		}

		PersonalInfo info = admin.getPersonalInfo();

		return AdminResponse.builder().id(admin.getAdminId()).fullName(info.getFullName()).email(info.getEmail())
				.mobileNumber(info.getMobileNumber()).employeeId(admin.getEmployeeId()).bio(info.getBio())
				.isActive(admin.getIsActive()).joiningDate(admin.getCreatedDate()).role(admin.getRole().getRoleName())
				.build();
	}
}
