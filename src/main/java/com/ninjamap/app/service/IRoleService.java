package com.ninjamap.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.RoleRequest;
import com.ninjamap.app.payload.request.UpdateRoleRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.RoleResponse;

public interface IRoleService {

	/** Create a new role with permissions */
	ResponseEntity<ApiResponse> createRole(RoleRequest request);

	/** Get all roles */
	ResponseEntity<List<RoleResponse>> getAllRoles();

	/** Get role by ID, optionally filtering by isActive */
	ResponseEntity<ApiResponse> getRoleById(String id, Boolean isActive);

	/** Update role details and permissions */
	ResponseEntity<ApiResponse> updateRole(String id, UpdateRoleRequest request);

	/** Update only role permissions */
//	ResponseEntity<ApiResponse> updateRolePermissions(String roleId, Set<String> permissionIds);
	ResponseEntity<ApiResponse> updateRolePermissionsByQuickActions(String roleId, Map<String, String> resourceActions);

	/** Soft delete a role */
	ResponseEntity<ApiResponse> deleteRole(String id);

}
