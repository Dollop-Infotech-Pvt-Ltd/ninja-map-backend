package com.ninjamap.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
	private String roleId;
	private String roleName;
	private String description;
//	private Set<PermissionResponse> permissions;
}
