package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TrimValidator
public class UpdatePolicyDocumentRequest {

	@NotBlank(message = ValidationConstants.POLICY_DOCUMENT_ID_REQUIRED)
	@UUIDValidator(message = ValidationConstants.INVALID_UUID)
	private String id;

	private String title;
	
//	@NotBlank(message = ValidationConstants.DOCUMENT_CONTENT_REQUIRED)
	private String description;



}