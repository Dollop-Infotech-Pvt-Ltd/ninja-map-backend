package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.Weekday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessHoursResponse {

	private String id;

	private Weekday weekday;

	private Boolean isOpen24Hours;

	private Boolean isClosed;

	private String openingTime;

	private String closingTime;
}
