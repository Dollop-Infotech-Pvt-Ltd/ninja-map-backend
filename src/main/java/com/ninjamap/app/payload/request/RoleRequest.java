package com.ninjamap.app.payload.request;

import java.util.List;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

	@NotNull(message = ValidationConstants.ROLE_NAME_REQUIRED)
	@Size(min = 3, max = 50, message = ValidationConstants.ROLE_NAME_SIZE)
	private String roleName;

	@NotNull(message = ValidationConstants.DESCRIPTION_REQUIRED)
	@Size(min = 5, max = 200, message = ValidationConstants.DESCRIPTION_SIZE)
	private String description;

	@NotEmpty(message = ValidationConstants.PERMISSION_IDS_REQUIRED)
	private List<String> permissionIds;
}
