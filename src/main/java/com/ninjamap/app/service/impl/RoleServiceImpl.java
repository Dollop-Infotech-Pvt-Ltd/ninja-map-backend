package com.ninjamap.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.PermissionType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Permission;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.payload.request.RoleRequest;
import com.ninjamap.app.payload.request.UpdateRoleRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.RoleResponse;
import com.ninjamap.app.repository.IPermissionRepository;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.service.IRoleService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

	private final IRolesRepository roleRepository;
	private final IPermissionRepository permissionRepository;

	@Override
	public ResponseEntity<ApiResponse> createRole(RoleRequest request) {
		String roleName = request.getRoleName().toUpperCase();
		if (roleRepository.existsByRoleNameAndIsDeletedFalse(roleName)) {
			throw new ResourceAlreadyExistException(AppConstants.ROLE_ALREADY_EXISTS);
		}

		List<Permission> permissions = permissionRepository
				.findAllByPermissionIdInAndIsDeletedFalse(request.getPermissionIds());

		if (permissions.size() != request.getPermissionIds().size()) {
			throw new ResourceNotFoundException("One or more permissions not found or deleted");
		}

		Roles role = Roles.builder().roleName(roleName.toLowerCase()).description(request.getDescription())
				.permissions(permissions).build();

		roleRepository.save(role);
		return ResponseEntity.ok(AppUtils.buildCreatedResponse(AppConstants.ROLE_CREATED));
	}

	private Roles findById(String id, Boolean isActive) {
		return roleRepository.findByRoleIdAndOptionalIsActive(id, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ROLE_NOT_FOUND));
	}

	@Override
	public ResponseEntity<ApiResponse> updateRole(String id, UpdateRoleRequest request) {
		Roles role = findById(id, null);

		if (roleRepository.existsByRoleNameAndRoleIdNotAndIsDeletedFalse(request.getRoleName(), id)) {
			throw new ResourceAlreadyExistException(AppConstants.ROLE_ALREADY_EXISTS);
		}

		role.setRoleName(request.getRoleName().toUpperCase());
		role.setDescription(request.getDescription());

		roleRepository.save(role);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ROLE_UPDATED));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse> updateRolePermissionsByQuickActions(String roleId,
			Map<String, String> resourceActions) {
		Roles role = findById(roleId, true);
		List<Permission> currentPermissions = new ArrayList<>(role.getPermissions());

		resourceActions.forEach((resourceKey, actionValue) -> {
			String normalizedResource = normalizeAction(resourceKey);
			currentPermissions.removeIf(p -> p.getResource().equalsIgnoreCase(normalizedResource));

			List<Permission> toAdd = fetchPermissionsByQuickAction(normalizedResource, actionValue);

			Set<String> existingPermissionIds = currentPermissions.stream().map(Permission::getPermissionId)
					.collect(Collectors.toSet());
			toAdd.stream().filter(p -> !existingPermissionIds.contains(p.getPermissionId()))
					.forEach(currentPermissions::add);
		});

		role.setPermissions(currentPermissions);
		roleRepository.save(role);

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ROLE_PERMISSION_UPDATED));
	}

	private String normalizeAction(String action) {
		return action.trim().toUpperCase().replaceAll("\\s+", "_");
	}

	private List<Permission> fetchPermissionsByQuickAction(String resource, String actionValue) {
		String normalizedAction = actionValue.trim();

		switch (normalizedAction.toUpperCase()) {
		case "SELECT ALL":
			return permissionRepository.findAllByResourceAndIsDeletedFalse(resource);
		case "DESELECT ALL":
			return Collections.emptyList();
		case "READ ONLY":
			return permissionRepository.findByResourceAndTypeInAndIsDeletedFalse(resource,
					List.of(PermissionType.READ));
		case "READ & WRITE":
			return permissionRepository.findByResourceAndTypeInAndIsDeletedFalse(resource,
					List.of(PermissionType.READ, PermissionType.WRITE));
		default:
			String[] actions = normalizedAction.split(",");
			List<String> normalizedActions = Arrays.stream(actions).map(String::trim).filter(s -> !s.isEmpty())
					.map(this::normalizeAction).toList();

			if (normalizedActions.isEmpty()) {
				throw new BadRequestException("Invalid action value: " + actionValue);
			}

			List<Permission> permissions = permissionRepository.findByResourceAndActionInAndIsDeletedFalse(resource,
					normalizedActions);

			if (permissions.isEmpty()) {
				throw new ResourceNotFoundException(
						"No permissions found for the specified actions: " + actionValue + " in resource: " + resource);
			}
			return permissions;
		}
	}

	@Override
	public ResponseEntity<List<RoleResponse>> getAllRoles() {
		List<RoleResponse> roles = roleRepository.findAllByIsDeletedFalse().stream().map(this::mapToRoleResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(roles);
	}

	@Override
	public ResponseEntity<ApiResponse> getRoleById(String id, Boolean isActive) {
		Roles role = findById(id, isActive);
		return ResponseEntity
				.ok(AppUtils.buildCreatedResponse(AppConstants.DATA_FATCH_SUCCESSFULLY, mapToRoleResponse(role)));
	}

	@Override
	public ResponseEntity<ApiResponse> deleteRole(String id) {
		Roles role = findById(id, null);
		role.setIsActive(false);
		role.setIsDeleted(true);
		roleRepository.save(role);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ROLE_DELETE_SUCCESS));
	}

	private RoleResponse mapToRoleResponse(Roles role) {
		return RoleResponse.builder().roleId(role.getRoleId()).roleName(role.getRoleName())
				.description(role.getDescription()).build();
	}
}
