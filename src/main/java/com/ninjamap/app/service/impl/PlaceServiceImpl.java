package com.ninjamap.app.service.impl;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ninjamap.app.config.CorsConfig;
import com.ninjamap.app.model.Category;
import com.ninjamap.app.model.Place;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.PlaceRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CategoryResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.PlaceResponse;
import com.ninjamap.app.repository.IPlaceRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IPlaceService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class PlaceServiceImpl implements IPlaceService {

    private final CorsConfig corsConfig;

	@Autowired
	private IPlaceRepository placeRepository;
	
	@Autowired
	private ICloudinaryService cloudinaryService;

	@Autowired
	private IUserService userService;

    PlaceServiceImpl(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

	@Override
	public ApiResponse addPlace(PlaceRequest placeRequest) {
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Validate: Either name or categoryId must be provided, not both
		boolean hasName = placeRequest.getName() != null && !placeRequest.getName().trim().isEmpty();

		if (!hasName) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.BAD_REQUEST.value())
					.message("Either place name must be provided")
					.data(null)
					.build();
		}
		Place place;
			// Check for duplicate custom place
			if (placeRepository.existsByUserIdAndNameCustom(userId, placeRequest.getName())) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.CONFLICT.value())
						.message("You already have place with this name")
						.data(null)
						.build();
			}

			place = Place.builder()
					.userId(userId)
					.name(placeRequest.getName())
					.address(placeRequest.getAddress())
					.emojiPic(placeRequest.getEmojiUrl())
					.latitude(placeRequest.getLatitude())
					.longitude(placeRequest.getLongitude())
					.build();
		placeRepository.save(place);

		return ApiResponse.builder()
				.statusCode(HttpStatus.CREATED.value())
				.message(AppConstants.PLACE_ADDED)
				.build();
	}

	@Override
	public ApiResponse getPlacesByUserId(PaginationRequest paginationRequest) {
		
		
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, Place.class);
		
		// Get current user ID
		String userId = userService.getCurrectUserFromToken().getId();

		// Get all places for user
		Page<Place> places = placeRepository.findByUserIdAndIsDeletedFalse(userId,pageable);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.PLACE_FTECH)
				.data(new PaginatedResponse(places.map(this::convertPlaceToResponse)))
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
		
		// Check for duplicate custom place
		if (placeRepository.existsByUserIdAndNameCustom(userId, updatePlaceRequest.getName(),placeId)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.CONFLICT.value())
					.message("You already have place with this name")
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
		
		
		
		System.err.println(updatePlaceRequest.getPlacePic());

		// Update only address, latitude, and longitude
		Place existingPlace = place.get();
		existingPlace.setName(updatePlaceRequest.getName());
		existingPlace.setEmojiPic(updatePlaceRequest.getPlacePic());
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
		return PlaceResponse.builder()
				.id(place.getId())
				.name(place.getName())
				.address(place.getAddress())
				.emojiPic(place.getEmojiPic())
				.latitude(place.getLatitude())
				.longitude(place.getLongitude())
				.createdDate(place.getCreatedDate())
				.updatedDate(place.getUpdatedDate())
				.build();
	}
}
