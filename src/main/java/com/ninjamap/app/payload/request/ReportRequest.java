package com.ninjamap.app.payload.request;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportType;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRequest {

	@NotNull(message = ValidationConstants.REPORT_TYPE_REQUIRED)
	private ReportType reportType;

	@NotBlank(message = ValidationConstants.REPORT_COMMENT_REQUIRED)
	@Size(min = 10, max = 5000, message = "Comment must be between 10 and 5000 characters")
	private String comment;

	@NotNull(message = ValidationConstants.REPORT_LATITUDE_REQUIRED)
	@DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
	@DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
	private Double latitude;

	@NotNull(message = ValidationConstants.REPORT_LONGITUDE_REQUIRED)
	@DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
	@DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
	private Double longitude;
	
	@NotNull(message = ValidationConstants.REPORT_LOCATION_REQUIRED)
	private String location;

	private Boolean hideName=false;
	
	private MultipartFile reportPicture;
}
