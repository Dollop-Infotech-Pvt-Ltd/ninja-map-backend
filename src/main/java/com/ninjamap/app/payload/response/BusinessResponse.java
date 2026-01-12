package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class BusinessResponse {

	private String id;

	private String businessName;

	private SimpleSubCategoryResponse subCategory;

	private String address;

	private Double latitude;

	private Double longitude;

	private String phoneNumber;

	private String website;

	private List<BusinessHoursResponse> businessHours;

	private List<BusinessImageResponse> businessImages;

	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;

	private Boolean isActive;
}
