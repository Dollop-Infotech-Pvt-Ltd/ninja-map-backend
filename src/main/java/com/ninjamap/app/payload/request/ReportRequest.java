package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportType;
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

	@NotNull(message = "Report type is required")
	private ReportType reportType;

	@NotNull(message = "Severity level is required")
	private ReportSeverity severity;

	@NotBlank(message = "Title is required")
	@Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
	private String description;

	@NotNull(message = "Latitude is required")
	@DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
	@DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
	private Double latitude;

	@NotNull(message = "Longitude is required")
	@DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
	@DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
	private Double longitude;
	
	private String location;

	@Size(max = 500, message = "Address cannot exceed 500 characters")
	private String address;
}
