package com.ninjamap.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Category;
import com.ninjamap.app.model.Place;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CategoryResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.PlaceListResponse;
import com.ninjamap.app.payload.response.PlaceResponse;
import com.ninjamap.app.repository.ICategoryRepository;
import com.ninjamap.app.repository.IPlaceRepository;
import com.ninjamap.app.service.IPlaceService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class PlaceServiceImpl implements IPlaceService {

	@Autowired
	private IPlaceRepository placeRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private IUserService userService;

	@Override
	public ApiResponse addPlace(PlaceRequest placeRequest) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Validate: Either name or categoryId must be provided, not both
		boolean hasName = placeRequest.getName() != null && !placeRequest.getName().trim().isEmpty();
		boolean hasCategoryId = placeRequest.getCategoryId() != null && !placeRequest.getCategoryId().trim().isEmpty();

		if (!hasName && !hasCategoryId) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.message("Either place name or category ID must be provided")
					.data(null)
					.build();
		}

		if (hasName && hasCategoryId) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.message("Cannot provide both name and category ID. Choose one type of place")
					.data(null)
					.build();
		}

		Place place;

		if (hasCategoryId) {
			// Category-based place
			Optional<Category> category = categoryRepository.findById(placeRequest.getCategoryId());
			if (category.isEmpty()) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message(AppConstants.CATEGORY_NOT_FOUND)
						.data(null)
						.build();
			}

			// Check for duplicate category-based place
			if (placeRepository.existsByUserIdAndCategoryId(userId, placeRequest.getCategoryId())) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.CONFLICT.value())
						.message("You already have a place with this category")
						.data(null)
						.build();
			}

			place = Place.builder()
					.userId(userId)
					.name(category.get().getCategoryName())
					.address(placeRequest.getAddress())
					.latitude(placeRequest.getLatitude())
					.longitude(placeRequest.getLongitude())
					.category(category.get())
					.placeType(Place.PlaceType.CATEGORY)
					.build();
		} else {
			// Custom place
			// Check for duplicate custom place
			if (placeRepository.existsByUserIdAndNameCustom(userId, placeRequest.getName())) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.CONFLICT.value())
						.message("You already have a custom place with this name")
						.data(null)
						.build();
			}

			place = Place.builder()
					.userId(userId)
					.name(placeRequest.getName())
					.address(placeRequest.getAddress())
					.latitude(placeRequest.getLatitude())
					.longitude(placeRequest.getLongitude())
					.category(null)
					.placeType(Place.PlaceType.CUSTOM)
					.build();
		}

		Place savedPlace = placeRepository.save(place);

		return ApiResponse.builder()
				.statusCode(HttpStatus.CREATED.value())
				.message(AppConstants.PLACE_ADDED)
				.data(convertPlaceToResponse(savedPlace))
				.build();
	}

	@Override
	public ApiResponse getPlacesByUserId(PaginationRequest paginationRequest) {
		
		
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, Place.class);
		
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get all places for user
		Page<Place> places = placeRepository.findByUserIdAndIsDeletedFalse(userId,pageable);

		// Calculate category counts (only for category-based places)
		Map<String, Integer> categoryCounts = new HashMap<>();
		for (Place place : places) {
			// Only count category-based places (custom places have null category)
			if (place.getCategory() != null) {
				String categoryName = place.getCategory().getCategoryName();
				categoryCounts.put(categoryName, categoryCounts.getOrDefault(categoryName, 0) + 1);
			}
		}

		PlaceListResponse response = PlaceListResponse.builder()
				.places(new PaginatedResponse(places.map(this::convertPlaceToResponse)))
				.categoriesCount(categoryCounts)
				.build();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.PLACE_FTECH)
				.data(response)
				.build();
	}

	@Override
	public ApiResponse getPlaceById(String placeId) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get place
		Optional<Place> place = placeRepository.findByIdAndIsDeletedFalse(placeId);
		if (place.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.PLACE_NOT_FOUND)
					.data(null)
					.build();
		}

		// Verify ownership
		if (!place.get().getUserId().equals(userId)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.FORBIDDEN.value())
					.message("You do not have permission to access this place")
					.data(null)
					.build();
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Place retrieved successfully")
				.data(convertPlaceToResponse(place.get()))
				.build();
	}

	@Override
	public ApiResponse getPlacesByCategory(String categoryId) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get places by user and category
		List<Place> places = placeRepository.findByUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId);

		// Convert to response DTOs
		List<PlaceResponse> placeResponses = places.stream()
				.map(this::convertPlaceToResponse)
				.toList();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.PLACE_FTECH)
				.data(placeResponses)
				.build();
	}

	@Override
	public ApiResponse updatePlace(String placeId, com.ninjamap.app.payload.request.UpdatePlaceRequest updatePlaceRequest) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get place
		Optional<Place> place = placeRepository.findByIdAndIsDeletedFalse(placeId);
		if (place.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.PLACE_NOT_FOUND)
					.data(null)
					.build();
		}

		// Verify ownership
		if (!place.get().getUserId().equals(userId)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.FORBIDDEN.value())
					.message("You do not have permission to update this place")
					.data(null)
					.build();
		}

		// Update only address, latitude, and longitude
		Place existingPlace = place.get();
		existingPlace.setAddress(updatePlaceRequest.getAddress());
		existingPlace.setLatitude(updatePlaceRequest.getLatitude());
		existingPlace.setLongitude(updatePlaceRequest.getLongitude());

		Place updatedPlace = placeRepository.save(existingPlace);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Place updated successfully")
				.data(convertPlaceToResponse(updatedPlace))
				.build();
	}

	@Override
	public ApiResponse deletePlace(String placeId) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get place
		Optional<Place> place = placeRepository.findByIdAndIsDeletedFalse(placeId);
		if (place.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.PLACE_NOT_FOUND)
					.data(null)
					.build();
		}

		// Verify ownership
		if (!place.get().getUserId().equals(userId)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.FORBIDDEN.value())
					.message("You do not have permission to delete this place")
					.data(null)
					.build();
		}

		// Soft delete place
		Place existingPlace = place.get();
		existingPlace.setIsDeleted(true);
		placeRepository.save(existingPlace);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.PLACE_DELETED)
				.data(null)
				.build();
	}

	@Override
	public ApiResponse getUnusedCategories() {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get all active categories
		List<Category> allCategories = categoryRepository.findByIsActiveTrue();

		// Get categories user has already added places for
		List<String> usedCategoryIds = placeRepository.findUsedCategoryIdsByUserId(userId);

		// Filter out used categories
		List<CategoryResponse> unusedCategories = allCategories.stream()
				.filter(category -> !usedCategoryIds.contains(category.getId()))
				.map(this::convertCategoryToResponse)
				.toList();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Unused categories retrieved successfully")
				.data(unusedCategories)
				.build();
	}

	/**
	 * Helper method to convert Category entity to CategoryResponse DTO
	 */
	private CategoryResponse convertCategoryToResponse(Category category) {
		return CategoryResponse.builder()
				.id(category.getId())
				.categoryName(category.getCategoryName())
				.categoryPicture(category.getCategoryPicture())
				.isActive(category.getIsActive())
				.createdDate(category.getCreatedDate())
				.updatedDate(category.getUpdatedDate())
				.build();
	}

	/**
	 * Helper method to convert Place entity to PlaceResponse DTO
	 */
	private PlaceResponse convertPlaceToResponse(Place place) {
		CategoryResponse categoryResponse = null;
		
		// Only populate category response if category exists (for category-based places)
		if (place.getCategory() != null) {
			categoryResponse = convertCategoryToResponse(place.getCategory());
		}

		return PlaceResponse.builder()
				.id(place.getId())
				.name(place.getName())
				.address(place.getAddress())
				.latitude(place.getLatitude())
				.longitude(place.getLongitude())
				.category(categoryResponse)
				.placeType(place.getPlaceType().toString())
				.createdDate(place.getCreatedDate())
				.updatedDate(place.getUpdatedDate())
				.build();
	}
}
