package com.ninjamap.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.RoleRequest;
import com.ninjamap.app.payload.request.UpdateRoleRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.RoleResponse;
import com.ninjamap.app.service.IRoleService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

	private final IRoleService roleService;

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.CREATE_ROLES')")
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createRole(@RequestBody @Valid RoleRequest request) {
		return roleService.createRole(request);
	}

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.EDIT_ROLES')")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateRole(
			@RequestParam(name = AppConstants.ROLE_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId,
			@RequestBody @Valid UpdateRoleRequest request) {
		return roleService.updateRole(roleId, request);
	}

//	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.ASSIGN_ROLES')")
//	@PutMapping("/update-permissions")
//	public ResponseEntity<ApiResponse> updateRolePermissions(
//			@RequestParam(name = AppConstants.ROLE_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId,
//			@RequestBody Set<String> permissionIds) {
//		return roleService.updateRolePermissions(roleId, permissionIds);
//	}

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.ASSIGN_ROLES')")
	@PutMapping("/update-permissions")
	public ResponseEntity<ApiResponse> updateRolePermissionsByQuickActions(
			@RequestParam(name = AppConstants.ROLE_ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId,
			@RequestBody Map<String, String> resourceActions) {
		return roleService.updateRolePermissionsByQuickActions(roleId, resourceActions);
	}

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.VIEW_ROLES')")
	@GetMapping("/get-all")
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		return roleService.getAllRoles();
	}

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.VIEW_ROLES')")
	@GetMapping("/get")
	public ResponseEntity<ApiResponse> getRoleById(
			@RequestParam(name = AppConstants.ROLE_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId,
			@RequestParam(name = AppConstants.IS_ACTIVE, required = false) Boolean isActive) {
		return roleService.getRoleById(roleId, isActive);
	}

	@PreAuthorize("hasAuthority('ROLE_MANAGEMENT.DELETE_ROLES')")
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteRole(
			@RequestParam(name = AppConstants.ROLE_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId) {
		return roleService.deleteRole(roleId);
	}
}
