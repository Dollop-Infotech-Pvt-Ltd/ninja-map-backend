package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IPlaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Validated
public class PlaceController {

	@Autowired
	private IPlaceService placeService;

	/**
	 * Create a new place
	 */
	@PostMapping("/add-place")
	public ResponseEntity<ApiResponse> addPlace(@Valid @RequestBody PlaceRequest placeRequest) {
		return new ResponseEntity<>(placeService.addPlace(placeRequest), HttpStatus.CREATED);
	}

	/**
	 * Get all places for the authenticated user
	 * Get my places
	 */
	@GetMapping("/get-places")
	public ResponseEntity<ApiResponse> getPlaces() {
		return new ResponseEntity<>(placeService.getPlacesByUserId(), HttpStatus.OK);
	}

	/**
	 * Get a specific place by ID
	 */
	@GetMapping("/get-places-id")
	public ResponseEntity<ApiResponse> getPlaceById(@RequestParam String id) {
		return new ResponseEntity<>(placeService.getPlaceById(id), HttpStatus.OK);
	}

	/**
	 * Get places filtered by category
	 */
	@GetMapping("/get-places-category")
	public ResponseEntity<ApiResponse> getPlacesByCategory(@RequestParam String categoryId) {
		return new ResponseEntity<>(placeService.getPlacesByCategory(categoryId), HttpStatus.OK);
	}

	/**
	 * Update an existing place
	 */
	@PutMapping("/update-place")
	public ResponseEntity<ApiResponse> updatePlace(@RequestParam String id,
			@Valid PlaceRequest placeRequest) {
		return new ResponseEntity<>(placeService.updatePlace(id, placeRequest), HttpStatus.OK);
	}

	/**
	 * Delete a place
	 */
	@DeleteMapping("/delete-place")
	public ResponseEntity<ApiResponse> deletePlace(@RequestParam String id) {
		return new ResponseEntity<>(placeService.deletePlace(id), HttpStatus.OK);
	}

	/**
	 * Get categories that user has not added places for
	 */
	@GetMapping("/unused-categories")
	public ResponseEntity<ApiResponse> getUnusedCategories() {
		return new ResponseEntity<>(placeService.getUnusedCategories(), HttpStatus.OK);
	}
}
