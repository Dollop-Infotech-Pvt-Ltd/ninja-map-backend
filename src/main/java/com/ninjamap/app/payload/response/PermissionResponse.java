package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.PermissionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
	private String permissionId;
	private String resource; // e.g., USER_MANAGEMENT
	private PermissionType type; // READ, WRITE, UPDATE, DELETE
	private String action; // e.g., VIEW_USERS
}
