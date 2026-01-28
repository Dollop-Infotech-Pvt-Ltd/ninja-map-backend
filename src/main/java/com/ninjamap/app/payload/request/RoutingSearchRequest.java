package com.ninjamap.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutingSearchRequest {

	private Double lat;
	private Double lon;
	private String searchTerm;
	private String searchRadius;
	private String costing;
	private Double useFerry;
	private Integer ferryCost;
}
