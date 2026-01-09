package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Category;
import com.ninjamap.app.model.SubCategory;
import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CategoryResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.SubCategoryResponse;
import com.ninjamap.app.repository.ICategoryRepository;
import com.ninjamap.app.repository.ISubCategoryRepository;
import com.ninjamap.app.service.ICategoryService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private ICategoryRepository categoryRepository;
	
	@Autowired
	private ISubCategoryRepository subCategoryRepository;
	
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
		
		Page<Category> activeCategories;
		if (paginationRequest.getSearchValue() != null && !paginationRequest.getSearchValue().trim().isEmpty()) {
			// Use search with subcategories loaded
			activeCategories = categoryRepository.findCategoriesWithSubCategoriesByFilters(
					paginationRequest.getSearchValue().trim(), pageable);
		} else {
			// Use method that loads subcategories for metadata calculation
			activeCategories = categoryRepository.findCategoriesWithSubCategories(pageable);
		}

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

		Category category = existingCategory.get();
		
		// Cascade delete: soft delete all subcategories belonging to this category
		List<SubCategory> subCategories = subCategoryRepository.findByCategoryIdAndIsActiveTrue(id);
		for (SubCategory subCategory : subCategories) {
			subCategory.setIsActive(false);
			subCategory.setIsDeleted(Boolean.TRUE);
		}
		if (!subCategories.isEmpty()) {
			subCategoryRepository.saveAll(subCategories);
		}

		// Soft delete the category by setting isActive to false
		category.setIsActive(false);
		category.setIsDeleted(Boolean.TRUE);
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
		// Calculate subcategory metadata
		List<SubCategory> activeSubCategories = category.getSubCategories().stream()
				.filter(subCategory -> subCategory.getIsActive() && !subCategory.getIsDeleted())
				.collect(Collectors.toList());
		

    List<SubCategoryResponse> responses = activeSubCategories.stream()
        .map(s -> SubCategoryResponse.builder()
                .id(s.getId())
                .subCategoryName(s.getSubCategoryName())
                .isActive(s.getIsActive())
                .createdDate(s.getCreatedDate())
                .updatedDate(s.getUpdatedDate())
                .build()
        )
        .collect(Collectors.toList());


		return CategoryResponse.builder()
				.id(category.getId())
				.categoryName(category.getCategoryName())
				.categoryPicture(category.getCategoryPicture())
				.isActive(category.getIsActive())
				.createdDate(category.getCreatedDate())
				.updatedDate(category.getUpdatedDate())
				.subCategories(responses) // Don't load full subcategories in regular endpoint
				.hasSubCategories(!activeSubCategories.isEmpty())
				.subCategoryCount(activeSubCategories.size())
				.build();
	}

	private CategoryResponse convertCategoryToResponseWithSubCategories(Category category) {
		List<SubCategoryResponse> subCategoryResponses = category.getSubCategories().stream()
				.filter(subCategory -> subCategory.getIsActive() && !subCategory.getIsDeleted())
				.map(this::convertSubCategoryToResponse)
				.collect(Collectors.toList());

		return CategoryResponse.builder()
				.id(category.getId())
				.categoryName(category.getCategoryName())
				.categoryPicture(category.getCategoryPicture())
				.isActive(category.getIsActive())
				.createdDate(category.getCreatedDate())
				.updatedDate(category.getUpdatedDate())
				.subCategories(subCategoryResponses)
				.hasSubCategories(!subCategoryResponses.isEmpty())
				.subCategoryCount(subCategoryResponses.size())
				.build();
	}

	private SubCategoryResponse convertSubCategoryToResponse(SubCategory subCategory) {
		return SubCategoryResponse.builder()
				.id(subCategory.getId())
				.subCategoryName(subCategory.getSubCategoryName())
				.isActive(subCategory.getIsActive())
				.createdDate(subCategory.getCreatedDate())
				.updatedDate(subCategory.getUpdatedDate())
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

	@Override
	public ApiResponse getCategoriesWithSubCategories(PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, Category.class);
		
		Page<Category> categoriesWithSubCategories;
		if (paginationRequest.getSearchValue() != null && !paginationRequest.getSearchValue().trim().isEmpty()) {
			categoriesWithSubCategories = categoryRepository.findCategoriesWithSubCategoriesByFilters(
					paginationRequest.getSearchValue().trim(), pageable);
		} else {
			categoriesWithSubCategories = categoryRepository.findCategoriesWithSubCategories(pageable);
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.CATEGORY_FTECH)
				.data(new PaginatedResponse<>(categoriesWithSubCategories.map(this::convertCategoryToResponseWithSubCategories)))
				.build();
	}



}
