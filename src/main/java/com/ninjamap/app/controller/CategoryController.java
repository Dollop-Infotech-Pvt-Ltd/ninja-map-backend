package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.ICategoryService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

	@Autowired
	private ICategoryService categoryService;

	@GetMapping("/get-categories")
	public ResponseEntity<ApiResponse> getCategories(
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
		
		return new ResponseEntity<>(this.categoryService.getCategories(paginationRequest), HttpStatus.OK);
	}

	@GetMapping("/get-category")
	public ResponseEntity<ApiResponse> getCategoryById(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return new ResponseEntity<>(this.categoryService.getCategoryById(id), HttpStatus.OK);
	}

	@PostMapping("/add-category")
	public ResponseEntity<ApiResponse> addCategory(@Valid CategoryRequest categoryRequest) {
		return new ResponseEntity<>(this.categoryService.addCategory(categoryRequest), HttpStatus.OK);
	}

	@PutMapping("/update-category")
	public ResponseEntity<ApiResponse> updateCategory(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@Valid CategoryRequest categoryRequest) {
		return new ResponseEntity<>(this.categoryService.updateCategory(id, categoryRequest), HttpStatus.OK);
	}

	@DeleteMapping("/delete-category")
	public ResponseEntity<ApiResponse> deleteCategory(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return new ResponseEntity<>(this.categoryService.deleteCategory(id), HttpStatus.OK);
	}

	@GetMapping("/admin/all")
	public ResponseEntity<ApiResponse> getAllCategoriesAdmin() {
		return new ResponseEntity<>(this.categoryService.getAllCategoriesAdmin(), HttpStatus.OK);
	}

	
}
