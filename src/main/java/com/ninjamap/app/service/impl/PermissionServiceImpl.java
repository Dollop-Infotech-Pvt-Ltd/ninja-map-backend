package com.ninjamap.app.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.enums.PermissionType;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Permission;
import com.ninjamap.app.payload.request.PermissionRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PermissionResponse;
import com.ninjamap.app.repository.IPermissionRepository;
import com.ninjamap.app.service.IPermissionService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

	private final IPermissionRepository permissionRepository;

	@Override
	public ResponseEntity<ApiResponse> createPermission(PermissionRequest request) {
		System.err.println("====> "+permissionRepository.existsByResourceAndActionAndIsDeletedFalse(request.getResource(),
				request.getAction()));
		
		if (permissionRepository.existsByResourceAndActionAndIsDeletedFalse(request.getResource(),
				request.getAction())) {
			throw new ResourceAlreadyExistException(AppConstants.PERMISSION_ALREADY_EXISTS);
		}

		Permission permission = Permission.builder().resource(request.getResource()).type(request.getType())
				.action(request.getAction()).build();

		permissionRepository.save(permission);

		return ResponseEntity.ok(AppUtils.buildCreatedResponse(AppConstants.PERMISSION_CREATED));
	}

	@Override
	public ResponseEntity<List<PermissionResponse>> getAllPermissions(String resource, String type,
			String searchKeyword) {
		return ResponseEntity.ok(permissionRepository
				.findAllWithOptionalFilters(toDbResourceFormat(resource), parsePermissionTypes(type), searchKeyword)
				.stream().map(this::mapToPermissionResponse).toList());
	}

	@Override
	public ResponseEntity<ApiResponse> getPermissionById(String id, Boolean isActive) {
		Permission permission = findById(id, isActive);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.PERMISSION_FETCHED_SUCCESSFULLY,
				mapToPermissionResponse(permission)));
	}

	private Permission findById(String id, Boolean isActive) {
		return permissionRepository.findByIdAndOptionalIsActive(id, isActive)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.PERMISSION_NOT_FOUND));
	}

	@Override
	public ResponseEntity<ApiResponse> updatePermission(String id, PermissionRequest request) {
		Permission permission = findById(id, null);

		boolean duplicateExists = permissionRepository.existsByResourceAndActionAndIsDeletedFalse(request.getResource(),
				request.getAction()) && !permission.getPermissionId().equals(id);

		if (duplicateExists) {
			throw new ResourceAlreadyExistException(AppConstants.PERMISSION_ALREADY_EXISTS);
		}

		permission.setResource(request.getResource());
		permission.setType(request.getType());
		permission.setAction(request.getAction());

		permissionRepository.save(permission);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.PERMISSION_UPDATE_SUCCESSFULLY));
	}

	@Override
	public ResponseEntity<ApiResponse> deletePermission(String id) {
		Permission permission = findById(id, true);
		permission.setIsActive(false);
		permission.setIsDeleted(true);
		permissionRepository.save(permission);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.PERMISSION_DELETE_SUCCESS));
	}

	@Override
	public PermissionResponse mapToPermissionResponse(Permission permission) {
		return PermissionResponse.builder().permissionId(permission.getPermissionId())
				.resource(permission.getResource()).type(permission.getType()).action(permission.getAction()).build();
	}

	@Override
	public ResponseEntity<List<PermissionResponse>> getAllPermissionByRoleId(String roleId, String resource,
			String type) {
		return ResponseEntity.ok(permissionRepository
				.findAllByRoleIdAndOptionalFilters(roleId, toDbResourceFormat(resource), parsePermissionTypes(type))
				.stream().map(this::mapToPermissionResponse).toList());
	}

	/**
	 * Converts a human-friendly resource name to DB format. Example: "User
	 * Management" -> "USER_MANAGEMENT"
	 */
	public String toDbResourceFormat(String resource) {
		if (resource == null || resource.isBlank() || "All".equalsIgnoreCase(resource)) {
			return null; // No filter
		}
		return resource.trim().replaceAll("\\s+", "_").toUpperCase();
	}

	private List<PermissionType> parsePermissionTypes(String type) {
		if (type == null || type.isBlank() || "All".equalsIgnoreCase(type)) {
			return null;
		}
		return Arrays.stream(type.split(" & ")).map(part -> AppUtils.parseEnum(PermissionType.class, part.trim()))
				.toList();
	}
}
