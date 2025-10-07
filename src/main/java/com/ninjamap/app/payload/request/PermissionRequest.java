package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.PermissionType;
import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TrimValidator
public class PermissionRequest {

	@NotBlank(message = ValidationConstants.RESOURCE_REQUIRED)
	@Pattern(regexp = ValidationConstants.PATTERN_FOR_CAPITAL, message = ValidationConstants.PATTERN_FOR_CAPITAL_PATTERN_MESSAGE)
	private String resource; // USER_MANAGEMENT, ROLE_MANAGEMENT etc.

	@Pattern(regexp = ValidationConstants.PATTERN_FOR_CAPITAL, message = ValidationConstants.PATTERN_FOR_CAPITAL_PATTERN_MESSAGE)
	@NotBlank(message = ValidationConstants.ACTION_REQUIRED)
	private String action; // VIEW_USERS, CREATE_ROLES etc.

	@NotNull(message = ValidationConstants.PERMISSION_TYPE_REQUIRED)
	private PermissionType type; // READ, WRITE, UPDATE, DELETE
}