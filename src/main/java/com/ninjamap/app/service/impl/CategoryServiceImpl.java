package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Category;
import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CategoryResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.ICategoryRepository;
import com.ninjamap.app.service.ICategoryService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private ICategoryRepository categoryRepository;
	
	@Autowired
	private ICloudinaryService cloudinaryService;

	@Override
	public ApiResponse addCategory(CategoryRequest categoryRequest) {
		// Check if category name already exists
		Optional<Category> existingCategory = categoryRepository
				.findByCategoryNameAndIsDeletedFalseAndIsActiveTrue(categoryRequest.getCategoryName());
		if (existingCategory.isPresent()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.CONFLICT.value())
					.message(AppConstants.CATEGORY_ALREADY_EXIST)
					.data(null)
					.build();
		}

		// Create new category
		categoryRepository.save(convertRequestToCategory(categoryRequest));

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.ADD_CATEGORY)
				.build();
	}

	@Override
	public ApiResponse getCategories(PaginationRequest paginationRequest) {
		
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, Category.class);
		
		Page<Category> activeCategories = categoryRepository.findByIsActiveTrue(pageable);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_FTECH)
				.data(new PaginatedResponse<>(activeCategories.map(this::convertCategoryToResponse)))
				.build();
	}

	@Override
	public ApiResponse getCategoryById(String id) {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.CATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_FTECH)
				.data(convertCategoryToResponse(category.get()))
				.build();
	}

	@Override
	public ApiResponse updateCategory(String id, CategoryRequest categoryRequest) {
		Optional<Category> existingCategory = categoryRepository.findById(id);
		if (existingCategory.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.CATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		// Check if new category name already exists (excluding current category)
		Optional<Category> duplicateCategory = categoryRepository
				.findByCategoryNameAndIsDeletedFalseAndIsActiveTrue(categoryRequest.getCategoryName());
		if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(id)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.CONFLICT.value())
					.message(AppConstants.CATEGORY_ALREADY_EXIST)
					.data(null)
					.build();
		}
		
		String categoryPictureUrl = null;
		if (categoryRequest.getCategoryPicture() != null && !categoryRequest.getCategoryPicture().isEmpty()) {
			categoryPictureUrl = cloudinaryService.uploadFile(categoryRequest.getCategoryPicture(), AppConstants.CATEGORY_PICTURE);
		}

		// Update category
		Category category = existingCategory.get();
		category.setCategoryName(categoryRequest.getCategoryName());
		category.setCategoryPicture(categoryPictureUrl);
		categoryRepository.save(category);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_UPDATE_SUCCESSFULLY)
				.build();
	}

	@Override
	public ApiResponse deleteCategory(String id) {
		Optional<Category> existingCategory = categoryRepository.findById(id);
		if (existingCategory.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.CATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		// Soft delete by setting isActive to false
		Category category = existingCategory.get();
		category.setIsActive(false);
		categoryRepository.save(category);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_DELETED)
				.build();
	}

	@Override
	public ApiResponse getAllCategoriesAdmin() {
		List<Category> allCategories = categoryRepository.findAll();
		List<CategoryResponse> responses = allCategories.stream()
				.map(this::convertCategoryToResponse)
				.toList();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_FTECH)
				.data(responses)
				.build();
	}

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

	private Category convertRequestToCategory(CategoryRequest categoryRequest) {
		
		String categoryPictureUrl = null;
		if (categoryRequest.getCategoryPicture() != null && !categoryRequest.getCategoryPicture().isEmpty()) {
			categoryPictureUrl = cloudinaryService.uploadFile(categoryRequest.getCategoryPicture(), AppConstants.CATEGORY_PICTURE);
		}
		
		return Category.builder()
				.categoryName(categoryRequest.getCategoryName())
				.categoryPicture(categoryPictureUrl)
				.build();
	}
	
	
}
