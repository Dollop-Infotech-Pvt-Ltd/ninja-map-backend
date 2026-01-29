package com.ninjamap.app.payload.response;
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
public class RoutingSearchHistoryResponse {
	private String id;

	private String userId;

	private String searchTerm;

	private Double lat;

	private Double lon;

	private Integer searchRadius;

	private String costing;

	private Double useFerry;

	private Integer ferryCost;
}
