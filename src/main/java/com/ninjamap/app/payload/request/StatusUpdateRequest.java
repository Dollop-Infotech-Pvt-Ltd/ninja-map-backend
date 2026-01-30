package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusUpdateRequest {

	@NotBlank(message = ValidationConstants.ID_REQUIRED)
	private String reportId;

	@NotNull(message = "Status cannot be null")
	private ReportStatus newStatus;
}
