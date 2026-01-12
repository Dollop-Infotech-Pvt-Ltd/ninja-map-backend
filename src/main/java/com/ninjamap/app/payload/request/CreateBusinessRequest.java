package com.ninjamap.app.payload.request;

import java.nio.channels.MulticastChannel;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBusinessRequest {

	@NotBlank(message = ValidationConstants.BUSINESS_NAME_REQUIRED)
	@Size(min = 1, max = 255, message = ValidationConstants.BUSINESS_NAME_SIZE)
	private String businessName;

	@NotBlank(message = ValidationConstants.BUSINESS_SUB_CATEGORY_REQUIRED)
	private String subCategoryId;

	@NotBlank(message = ValidationConstants.BUSINESS_ADDRESS_REQUIRED)
	@Size(min = 1, max = 500, message = ValidationConstants.BUSINESS_ADDRESS_SIZE)
	private String address;

	@NotNull(message = ValidationConstants.BUSINESS_LATITUDE_REQUIRED)
	@DecimalMin(value = "-90.0", message = ValidationConstants.BUSINESS_LATITUDE_RANGE)
	@DecimalMax(value = "90.0", message = ValidationConstants.BUSINESS_LATITUDE_RANGE)
	private Double latitude;

	@NotNull(message = ValidationConstants.BUSINESS_LONGITUDE_REQUIRED)
	@DecimalMin(value = "-180.0", message = ValidationConstants.BUSINESS_LONGITUDE_RANGE)
	@DecimalMax(value = "180.0", message = ValidationConstants.BUSINESS_LONGITUDE_RANGE)
	private Double longitude;

	@NotBlank(message = ValidationConstants.BUSINESS_PHONE_REQUIRED)
	@Pattern(regexp = ValidationConstants.BUSINESS_PHONE_PATTERN, message = ValidationConstants.BUSINESS_PHONE_PATTERN_MESSAGE)
	private String phoneNumber;

	@Pattern(regexp = ValidationConstants.BUSINESS_WEBSITE_PATTERN, message = ValidationConstants.BUSINESS_WEBSITE_PATTERN_MESSAGE)
	private String website;

	private List<BusinessHoursRequest> businessHours;
	
	private List<MultipartFile> businessImages;
}
