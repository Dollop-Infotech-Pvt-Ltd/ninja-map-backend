package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.payload.request.BlogPostRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BlogPostResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IBlogPostService {
	ResponseEntity<ApiResponse> createPost(BlogPostRequest request);

	ResponseEntity<ApiResponse> updateBlogPost(String id, BlogPostRequest request);

	ResponseEntity<ApiResponse> deleteBlogPost(String id);

	ResponseEntity<ApiResponse> getPostById(String id);

	ResponseEntity<PaginatedResponse<BlogPostResponse>> getAllPosts(BlogCategory blogCategory,
			PaginationRequest paginationRequest);

}
