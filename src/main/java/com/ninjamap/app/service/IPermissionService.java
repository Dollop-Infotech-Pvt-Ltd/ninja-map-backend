package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.model.Permission;
import com.ninjamap.app.payload.request.PermissionRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PermissionResponse;

public interface IPermissionService {

	ResponseEntity<ApiResponse> createPermission(PermissionRequest request);

	ResponseEntity<List<PermissionResponse>> getAllPermissions(String resource, String type, String searchKeyword);

	ResponseEntity<ApiResponse> getPermissionById(String id, Boolean isActive);

	ResponseEntity<ApiResponse> updatePermission(String id, PermissionRequest request);

	ResponseEntity<ApiResponse> deletePermission(String id);

	ResponseEntity<List<PermissionResponse>> getAllPermissionByRoleId(String roleId, String resouce, String type);

	PermissionResponse mapToPermissionResponse(Permission permission);

}
