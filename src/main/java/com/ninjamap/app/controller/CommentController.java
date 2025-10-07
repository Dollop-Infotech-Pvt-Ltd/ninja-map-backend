package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.CommentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CommentResponse;
import com.ninjamap.app.service.ICommentService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

	private final ICommentService commentService;

	// Add comment to a blog post
	@PostMapping("/add-comment")
	public ResponseEntity<ApiResponse> addComment(
			@RequestParam(name = AppConstants.BLOG_POST_ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String blogPostId,
			@Valid @RequestBody CommentRequest request) {
		return commentService.addComment(blogPostId, request);
	}

	// Get all comments for a blog post with optional pagination
	@GetMapping("/get")
	public ResponseEntity<List<CommentResponse>> getComments(
			@RequestParam(name = AppConstants.BLOG_POST_ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String blogPostId) {
		return commentService.getCommentsByBlogPostId(blogPostId);
	}

	// Soft delete a comment
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteComment(
			@RequestParam(name = AppConstants.COMMENT_ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String commentId) {
		return commentService.deleteComment(commentId);
	}
}
