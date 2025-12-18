package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Category;
import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CategoryResponse;
import com.ninjamap.app.repository.ICategoryRepository;
import com.ninjamap.app.service.ICategoryService;
import com.ninjamap.app.service.ICloudinaryService;
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
					.message("Category name already exists")
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
	public ApiResponse getCategories() {
		List<Category> activeCategories = categoryRepository.findByIsActiveTrue();
		List<CategoryResponse> responses = activeCategories.stream()
				.map(this::convertCategoryToResponse)
				.toList();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Categories fetched successfully")
				.data(responses)
				.build();
	}

	@Override
	public ApiResponse getCategoryById(String id) {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message("Category not found")
					.data(null)
					.build();
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Category fetched successfully")
				.data(convertCategoryToResponse(category.get()))
				.build();
	}

	@Override
	public ApiResponse updateCategory(String id, CategoryRequest categoryRequest) {
		Optional<Category> existingCategory = categoryRepository.findById(id);
		if (existingCategory.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message("Category not found")
					.data(null)
					.build();
		}

		// Check if new category name already exists (excluding current category)
		Optional<Category> duplicateCategory = categoryRepository
				.findByCategoryNameAndIsDeletedFalseAndIsActiveTrue(categoryRequest.getCategoryName());
		if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(id)) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.CONFLICT.value())
					.message("Category name already exists")
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
		Category updatedCategory = categoryRepository.save(category);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message("Category updated successfully")
				.data(convertCategoryToResponse(updatedCategory))
				.build();
	}

	@Override
	public ApiResponse deleteCategory(String id) {
		Optional<Category> existingCategory = categoryRepository.findById(id);
		if (existingCategory.isEmpty()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message("Category not found")
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
				.message("All categories fetched successfully")
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
