package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.payload.request.BlogPostRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IBlogPostService {
	ResponseEntity<ApiResponse> createPost(BlogPostRequest request);

	ResponseEntity<ApiResponse> updateBlogPost(String id, BlogPostRequest request);

	ResponseEntity<ApiResponse> deleteBlogPost(String id);

	ResponseEntity<ApiResponse> getPostById(String id);

	ResponseEntity<ApiResponse> sharePost(String postId);

	ResponseEntity<ApiResponse> toggleSave(String postId, Boolean save);

	ResponseEntity<ApiResponse> toggleLike(String postId, Boolean like);

	ResponseEntity<ApiResponse> getHomepagePosts(BlogCategory category, PaginationRequest paginationRequest);

	ResponseEntity<ApiResponse> addView(String postId);

}
