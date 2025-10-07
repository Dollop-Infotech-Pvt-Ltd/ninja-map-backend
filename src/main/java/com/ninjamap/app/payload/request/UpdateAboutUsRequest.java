package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TrimValidator
public class UpdateAboutUsRequest {

//	@NotBlank(message = ValidationConstants.ID_REQUIRED)
//	@UUIDValidator(message = ValidationConstants.INVALID_UUID)
//	private String id;

//	@NotBlank(message = ValidationConstants.CONTENT_REQUIRED)
	private String content;

}
