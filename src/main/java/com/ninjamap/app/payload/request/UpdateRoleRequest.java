package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

	@NotNull(message = ValidationConstants.ROLE_NAME_REQUIRED)
	@Size(min = 3, max = 50, message = ValidationConstants.ROLE_NAME_SIZE)
	private String roleName;

	@NotNull(message = ValidationConstants.DESCRIPTION_REQUIRED)
	@Size(min = 5, max = 200, message = ValidationConstants.DESCRIPTION_SIZE)
	private String description;
}
