package com.ninjamap.app.payload.request;

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
public class PlaceRequest {

	@NotBlank(message = ValidationConstants.PLACE_NAME_REQUIRED)
	@Size(min = 1, max = 255, message = ValidationConstants.PLACE_NAME_SIZE)
	private String name;

	@NotBlank(message = ValidationConstants.PLACE_ADDRESS_REQUIRED)
	@Size(min = 1, max = 500,  message = ValidationConstants.PLACE_ADDRESS_SIZE)
	private String address;

	@NotNull(message = ValidationConstants.PLACE_LATITUDE_REQUIRED)
	@DecimalMin(value = "-90.0", message =  ValidationConstants.PLACE_LATITUDE_RANGE)
	@DecimalMax(value = "90.0", message = ValidationConstants.PLACE_LATITUDE_RANGE)
	private Double latitude;

	@NotNull(message = ValidationConstants.PLACE_EMOJI_REQUIRED)
	private String emojiUrl;

	@NotNull(message = ValidationConstants.PLACE_LONGITUDE_REQUIRED)
	@DecimalMin(value = "-180.0", message = ValidationConstants.PLACE_LONGITUDE_RANGE)
	@DecimalMax(value = "180.0", message = ValidationConstants.PLACE_LONGITUDE_RANGE)
	private Double longitude;

}
