package com.ninjamap.app.payload.request;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.DocumentType;
import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TrimValidator
public class CreatePolicyDocumentRequest {

	@NotNull(message = ValidationConstants.DOCUMENT_TYPE_REQUIRED)
	private DocumentType type;

	@NotBlank(message = ValidationConstants.TITLE_REQUIRED)
	private String title;

	@NotBlank(message = ValidationConstants.DOCUMENT_CONTENT_REQUIRED)
	private String description;

	@NotNull(message = ValidationConstants.IMAGE_REQUIRED)
	private MultipartFile documentImage;

}
