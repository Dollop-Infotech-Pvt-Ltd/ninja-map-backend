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
import com.ninjamap.app.payload.request.SubCategoryRequest;
import com.ninjamap.app.payload.request.UpdateSubCategoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.ISubCategoryService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
@Validated
public class SubCategoryController {

	@Autowired
	private ISubCategoryService subCategoryService;

	@GetMapping("/get-subcategories")
	public ResponseEntity<ApiResponse> getSubCategories(
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {
		
		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.searchValue(searchValue)
				.build();
		
		return new ResponseEntity<>(this.subCategoryService.getSubCategories(paginationRequest), HttpStatus.OK);
	}

	@GetMapping("/get-subcategory")
	public ResponseEntity<ApiResponse> getSubCategoryById(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return new ResponseEntity<>(this.subCategoryService.getSubCategoryById(id), HttpStatus.OK);
	}

	@PostMapping("/add-subcategory")
	public ResponseEntity<ApiResponse> addSubCategory(@Valid @RequestBody SubCategoryRequest subCategoryRequest) {
		return new ResponseEntity<>(this.subCategoryService.addSubCategory(subCategoryRequest), HttpStatus.OK);
	}

	@PutMapping("/update-subcategory")
	public ResponseEntity<ApiResponse> updateSubCategory(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@Valid @RequestBody UpdateSubCategoryRequest updateSubCategoryRequest) {
		return new ResponseEntity<>(this.subCategoryService.updateSubCategory(id, updateSubCategoryRequest), HttpStatus.OK);
	}

	@DeleteMapping("/delete-subcategory")
	public ResponseEntity<ApiResponse> deleteSubCategory(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return new ResponseEntity<>(this.subCategoryService.deleteSubCategory(id), HttpStatus.OK);
	}

	@GetMapping("/by-category")
	public ResponseEntity<ApiResponse> getSubCategoriesByCategory(
			@RequestParam(name = "categoryId") @UUIDValidator(message = ValidationConstants.INVALID_UUID) String categoryId,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey) {
		
		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.build();
		
		return new ResponseEntity<>(this.subCategoryService.getSubCategoriesByCategory(categoryId, paginationRequest), HttpStatus.OK);
	}


	@GetMapping("/admin/all")
	public ResponseEntity<ApiResponse> getAllSubCategoriesAdmin() {
		return new ResponseEntity<>(this.subCategoryService.getAllSubCategoriesAdmin(), HttpStatus.OK);
	}

}