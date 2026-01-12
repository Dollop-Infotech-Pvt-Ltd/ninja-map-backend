package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.Weekday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessHoursRequest {

	private Weekday weekday;

	private Boolean isOpen24Hours;

	private Boolean isClosed;

	private String openingTime;

	private String closingTime;
}
