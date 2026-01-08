package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IPlaceService {

	/**
	 * Add a new place for the authenticated user
	 */
	ApiResponse addPlace(PlaceRequest placeRequest);

	/**
	 * Get all places for the authenticated user
	 */
	ApiResponse getPlacesByUserId(PaginationRequest paginationRequest);

	/**
	 * Get a specific place by ID
	 */
	ApiResponse getPlaceById(String placeId);

	/**
	 * Update an existing place (only address, latitude, longitude can be updated)
	 */
	ApiResponse updatePlace(String placeId, com.ninjamap.app.payload.request.UpdatePlaceRequest updatePlaceRequest);

	/**
	 * Delete a place
	 */
	ApiResponse deletePlace(String placeId);
	
	
	ApiResponse getPlacesFilter();

}
