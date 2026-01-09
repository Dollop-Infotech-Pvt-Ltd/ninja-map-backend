package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Category;
import com.ninjamap.app.model.SubCategory;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SubCategoryRequest;
import com.ninjamap.app.payload.request.UpdateSubCategoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.SubCategoryResponse;
import com.ninjamap.app.repository.ICategoryRepository;
import com.ninjamap.app.repository.ISubCategoryRepository;
import com.ninjamap.app.service.ISubCategoryService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

	@Autowired
	private ISubCategoryRepository subCategoryRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Override
	public ApiResponse addSubCategory(SubCategoryRequest subCategoryRequest) {
		// Validate category exists
		Optional<Category> category = categoryRepository.findById(subCategoryRequest.getCategoryId());
		if (category.isEmpty() || !category.get().getIsActive() || category.get().getIsDeleted()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.CATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		// Check if subcategory name already exists in this category
		Optional<SubCategory> existingSubCategory = subCategoryRepository
				.findBySubCategoryNameAndCategoryIdAndIsDeletedFalseAndIsActiveTrue(
						subCategoryRequest.getSubCategoryName(), 
						subCategoryRequest.getCategoryId());
		if (existingSubCategory.isPresent()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.CONFLICT.value())
					.message(AppConstants.SUBCATEGORY_ALREADY_EXIST)
					.data(null)
					.build();
		}

		// Create new subcategory
		SubCategory subCategory = convertRequestToSubCategory(subCategoryRequest, category.get());
		subCategoryRepository.save(subCategory);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.ADD_SUBCATEGORY)
				.build();
	}

	@Override
	public ApiResponse getSubCategories(PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, SubCategory.class);
		
		Page<SubCategory> activeSubCategories;
		if (paginationRequest.getSearchValue() != null && !paginationRequest.getSearchValue().trim().isEmpty()) {
			activeSubCategories = subCategoryRepository.findAllByFilters(
					paginationRequest.getSearchValue().trim(), pageable);
		} else {
			activeSubCategories = subCategoryRepository.findByIsActiveTrue(pageable);
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.SUBCATEGORY_FTECH)
				.data(new PaginatedResponse<>(activeSubCategories.map(this::convertSubCategoryToResponse)))
				.build();
	}

	@Override
	public ApiResponse getSubCategoryById(String id) {
		Optional<SubCategory> subCategory = subCategoryRepository.findById(id);
		if (subCategory.isEmpty() || !subCategory.get().getIsActive() || subCategory.get().getIsDeleted()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.SUBCATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.SUBCATEGORY_FTECH)
				.data(convertSubCategoryToResponse(subCategory.get()))
				.build();
	}

	@Override
	public ApiResponse updateSubCategory(String id, UpdateSubCategoryRequest updateSubCategoryRequest) {
		Optional<SubCategory> existingSubCategory = subCategoryRepository.findById(id);
		if (existingSubCategory.isEmpty() || !existingSubCategory.get().getIsActive() || existingSubCategory.get().getIsDeleted()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.SUBCATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		SubCategory subCategory = existingSubCategory.get();
		String originalCategoryId = subCategory.getCategory().getId();
		boolean categoryChanged = false;

		// If categoryId is provided and different, validate new category and move subcategory
		if (updateSubCategoryRequest.getCategoryId() != null && 
			!updateSubCategoryRequest.getCategoryId().equals(originalCategoryId)) {
			
			Optional<Category> newCategory = categoryRepository.findById(updateSubCategoryRequest.getCategoryId());
			if (newCategory.isEmpty() || !newCategory.get().getIsActive() || newCategory.get().getIsDeleted()) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.NOT_FOUND.value())
						.message(AppConstants.CATEGORY_NOT_FOUND)
						.data(null)
						.build();
			}

			// Check if subcategory name already exists in the new category
			Optional<SubCategory> duplicateInNewCategory = subCategoryRepository
					.findBySubCategoryNameAndCategoryIdAndIsDeletedFalseAndIsActiveTrue(
							updateSubCategoryRequest.getSubCategoryName(), 
							updateSubCategoryRequest.getCategoryId());
			if (duplicateInNewCategory.isPresent() && !duplicateInNewCategory.get().getId().equals(id)) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.CONFLICT.value())
						.message(AppConstants.SUBCATEGORY_ALREADY_EXIST)
						.data(null)
						.build();
			}

			subCategory.setCategory(newCategory.get());
			categoryChanged = true;
		} else {
			// Check if new subcategory name already exists in current category (excluding current subcategory)
			Optional<SubCategory> duplicateSubCategory = subCategoryRepository
					.findBySubCategoryNameAndCategoryIdAndIsDeletedFalseAndIsActiveTrue(
							updateSubCategoryRequest.getSubCategoryName(), 
							originalCategoryId);
			if (duplicateSubCategory.isPresent() && !duplicateSubCategory.get().getId().equals(id)) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.CONFLICT.value())
						.message(AppConstants.SUBCATEGORY_ALREADY_EXIST)
						.data(null)
						.build();
			}
		}

		// Update subcategory name
		subCategory.setSubCategoryName(updateSubCategoryRequest.getSubCategoryName());
		subCategoryRepository.save(subCategory);

		String message = categoryChanged ? AppConstants.SUBCATEGORY_MOVED_SUCCESSFULLY : AppConstants.SUBCATEGORY_UPDATE_SUCCESSFULLY;
		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(message)
				.build();
	}

	@Override
	public ApiResponse deleteSubCategory(String id) {
		Optional<SubCategory> existingSubCategory = subCategoryRepository.findById(id);
		if (existingSubCategory.isEmpty() || existingSubCategory.get().getIsDeleted()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.SUBCATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		// Soft delete by setting isActive to false
		SubCategory subCategory = existingSubCategory.get();
		subCategory.setIsDeleted(Boolean.TRUE);
		subCategory.setIsActive(Boolean.FALSE);
		subCategoryRepository.save(subCategory);

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.SUBCATEGORY_DELETED)
				.build();
	}

	@Override
	public ApiResponse getSubCategoriesByCategory(String categoryId, PaginationRequest paginationRequest) {
		// Validate category exists
		Optional<Category> category = categoryRepository.findById(categoryId);
		if (category.isEmpty() || !category.get().getIsActive() || category.get().getIsDeleted()) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.NOT_FOUND.value())
					.message(AppConstants.CATEGORY_NOT_FOUND)
					.data(null)
					.build();
		}

		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, SubCategory.class);
		Page<SubCategory> subCategories;
		
		if (paginationRequest.getSearchValue() != null && !paginationRequest.getSearchValue().trim().isEmpty()) {
			subCategories = subCategoryRepository.findByCategoryIdAndFilters(
					categoryId, paginationRequest.getSearchValue().trim(), pageable);
		} else {
			subCategories = subCategoryRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
		}

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.SUBCATEGORY_FTECH)
				.data(new PaginatedResponse<>(subCategories.map(this::convertSubCategoryToResponse)))
				.build();
	}

	@Override
	public ApiResponse getAllSubCategoriesAdmin() {
		List<SubCategory> allSubCategories = subCategoryRepository.findAll();
		List<SubCategoryResponse> responses = allSubCategories.stream()
				.map(this::convertSubCategoryToResponse)
				.toList();

		return ApiResponse.builder()
				.statusCode(HttpStatus.OK.value())
				.message(AppConstants.SUBCATEGORY_FTECH)
				.data(responses)
				.build();
	}

	
	private SubCategoryResponse convertSubCategoryToResponse(SubCategory subCategory) {
		return SubCategoryResponse.builder()
				.id(subCategory.getId())
				.subCategoryName(subCategory.getSubCategoryName())
				.categoryId(subCategory.getCategory().getId())
				.categoryName(subCategory.getCategory().getCategoryName())
				.isActive(subCategory.getIsActive())
				.createdDate(subCategory.getCreatedDate())
				.updatedDate(subCategory.getUpdatedDate())
				.build();
	}

	private SubCategory convertRequestToSubCategory(SubCategoryRequest subCategoryRequest, Category category) {
		return SubCategory.builder()
				.subCategoryName(subCategoryRequest.getSubCategoryName())
				.category(category)
				.build();
	}
}