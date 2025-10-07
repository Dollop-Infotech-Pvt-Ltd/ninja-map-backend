package com.ninjamap.app.payload.request;
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
public class ContactUsUpdateStatusRequest {

	@NotNull(message = ValidationConstants.STATUS_NOT_NULL)
	private String status;

	@NotBlank(message = ValidationConstants.UPDATED_BY_NOT_BLANK)
	private String updatedBy;
}
