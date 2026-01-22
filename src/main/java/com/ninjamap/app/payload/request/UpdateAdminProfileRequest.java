package com.ninjamap.app.payload.request;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAdminProfileRequest {
	@NotBlank(message = ValidationConstants.ID_REQUIRED)
	@UUIDValidator(message = ValidationConstants.INVALID_UUID)
	private String id;

	@NotBlank(message = ValidationConstants.FIRST_NAME_REQUIRED)
	private String firstName;

	@NotBlank(message = ValidationConstants.LAST_NAME_REQUIRED)
	private String lastName;

	private MultipartFile profilePicture;

	private String gender;

	private String bio;

}
