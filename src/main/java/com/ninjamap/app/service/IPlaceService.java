package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IPlaceService {

	/**
	 * Add a new place for the authenticated user
	 */
	ApiResponse addPlace(PlaceRequest placeRequest);

	/**
	 * Get all places for the authenticated user
	 */
	ApiResponse getPlacesByUserId();

	/**
	 * Get a specific place by ID
	 */
	ApiResponse getPlaceById(String placeId);

	/**
	 * Get places filtered by category
	 */
	ApiResponse getPlacesByCategory(String categoryId);

	/**
	 * Update an existing place
	 */
	ApiResponse updatePlace(String placeId, PlaceRequest placeRequest);

	/**
	 * Delete a place
	 */
	ApiResponse deletePlace(String placeId);

	/**
	 * Get categories that user has not added places for
	 */
	ApiResponse getUnusedCategories();
}
