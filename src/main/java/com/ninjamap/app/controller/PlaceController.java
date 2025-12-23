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
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.request.UpdatePlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IPlaceService;
import com.ninjamap.app.utils.constants.AppConstants;
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
	public ResponseEntity<ApiResponse> getPlaces(
			@RequestParam(name = AppConstants.PAGE_SIZE,defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER,defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue
			) {
		
		PaginationRequest paginationRequest = PaginationRequest.builder().pageSize(pageSize).pageNumber(pageNumber)
				.sortDirection(sortDirection).sortKey(sortKey).searchValue(searchValue).build();
		
		return new ResponseEntity<>(placeService.getPlacesByUserId(paginationRequest), HttpStatus.OK);
	}

	/**
	 * Get a specific place by ID
	 */
	@GetMapping("/get-places-id")
	public ResponseEntity<ApiResponse> getPlaceById(@RequestParam String id) {
		return new ResponseEntity<>(placeService.getPlaceById(id), HttpStatus.OK);
	}

	/**
	 * Update an existing place (only address, latitude, longitude can be updated)
	 */
	@PutMapping("/update-place")
	public ResponseEntity<ApiResponse> updatePlace(@RequestParam String id,
			@Valid UpdatePlaceRequest updatePlaceRequest) {
		return new ResponseEntity<>(placeService.updatePlace(id, updatePlaceRequest), HttpStatus.OK);
	}

	/**
	 * Delete a place
	 */
	@DeleteMapping("/delete-place")
	public ResponseEntity<ApiResponse> deletePlace(@RequestParam String id) {
		return new ResponseEntity<>(placeService.deletePlace(id), HttpStatus.OK);
	}

}
