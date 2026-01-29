package com.ninjamap.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoutingSearchRequest {

	private Double lat;
	private Double lon;
	private String searchTerm;
	private Integer searchRadius;
	private String costing;
	private Double useFerry;
	private Integer ferryCost;
}
