package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.payload.request.BlogPostRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BlogPostResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.service.IBlogPostService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Validated
public class BlogPostController {

	private final IBlogPostService blogPostService;

	// Create new blog post
	@PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.CREATE_BLOGS')")
	@PostMapping("/post")
	public ResponseEntity<ApiResponse> createBlogPost(@Valid BlogPostRequest request) {
		return blogPostService.createPost(request);
	}

	// Update existing blog post
	@PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.EDIT_BLOGS')")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateBlogPost(
			@RequestParam(name = AppConstants.ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@Valid BlogPostRequest request) {
		return blogPostService.updateBlogPost(id, request);
	}

	// Soft delete a blog post
	@PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.DELETE_BLOGS')")

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteBlogPost(
			@RequestParam(name = AppConstants.ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return blogPostService.deleteBlogPost(id);
	}

	// Get single blog post by ID
	@GetMapping("/get")
	public ResponseEntity<ApiResponse> getBlogPostById(
			@RequestParam(name = AppConstants.ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return blogPostService.getPostById(id);
	}

	// Get all posts with optional category filter and pagination
	@GetMapping("/get-all")
	public ResponseEntity<PaginatedResponse<BlogPostResponse>> getAllBlogPosts(
			@RequestParam(required = false) BlogCategory category,
			@RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey
//			,@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue
	) {

		PaginationRequest paginationRequest = PaginationRequest.builder().pageSize(pageSize).pageNumber(pageNumber)
				.sortDirection(sortDirection).sortKey(sortKey)
//				.searchValue(searchValue)
				.build();

		return blogPostService.getAllPosts(category, paginationRequest);
	}
}
