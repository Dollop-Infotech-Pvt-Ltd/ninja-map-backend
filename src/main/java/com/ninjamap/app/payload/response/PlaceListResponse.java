package com.ninjamap.app.payload.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceListResponse {

	private List<PlaceResponse> places;

	private Map<String, Integer> categoriesCount;
}
