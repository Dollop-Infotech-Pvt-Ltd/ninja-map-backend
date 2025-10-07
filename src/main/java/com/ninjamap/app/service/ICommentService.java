package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.model.Comment;
import com.ninjamap.app.payload.request.CommentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CommentResponse;

public interface ICommentService {
	ResponseEntity<ApiResponse> addComment(String blogPostId, CommentRequest comment);

	ResponseEntity<List<CommentResponse>> getCommentsByBlogPostId(String blogPostId);

	ResponseEntity<ApiResponse> deleteComment(String commentId);

	CommentResponse mapToCommentRespons(Comment comment);
}
