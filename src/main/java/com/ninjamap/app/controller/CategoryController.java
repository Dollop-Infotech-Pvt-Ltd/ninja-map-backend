package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.CategoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.ICategoryService;

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
	public ResponseEntity<ApiResponse> getCategories() {
		return new ResponseEntity<>(this.categoryService.getCategories(), HttpStatus.OK);
	}

	@GetMapping("/get-category")
	public ResponseEntity<ApiResponse> getCategoryById(@RequestParam String id) {
		return new ResponseEntity<>(this.categoryService.getCategoryById(id), HttpStatus.OK);
	}

	@PostMapping("/add-category")
	public ResponseEntity<ApiResponse> addCategory(@Valid CategoryRequest categoryRequest) {
		return new ResponseEntity<>(this.categoryService.addCategory(categoryRequest), HttpStatus.OK);
	}

	@PutMapping("/update-category")
	public ResponseEntity<ApiResponse> updateCategory(@RequestParam String id,
			@Valid CategoryRequest categoryRequest) {
		return new ResponseEntity<>(this.categoryService.updateCategory(id, categoryRequest), HttpStatus.OK);
	}

	@DeleteMapping("/delete-category")
	public ResponseEntity<ApiResponse> deleteCategory(@RequestParam String id) {
		return new ResponseEntity<>(this.categoryService.deleteCategory(id), HttpStatus.OK);
	}

	@GetMapping("/admin/all")
	public ResponseEntity<ApiResponse> getAllCategoriesAdmin() {
		return new ResponseEntity<>(this.categoryService.getAllCategoriesAdmin(), HttpStatus.OK);
	}
	
}
