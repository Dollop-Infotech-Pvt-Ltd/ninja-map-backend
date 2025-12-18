package com.ninjamap.app.payload.response;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.ninjamap.app.model.Place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceListResponse {

	private PaginatedResponse<Place> places;

	private Map<String, Integer> categoriesCount;
}
