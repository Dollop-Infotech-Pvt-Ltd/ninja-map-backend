package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // ========================= ADD COMMENT =========================
    @PreAuthorize("hasAuthority('COMMENT_MANAGEMENT.CREATE_COMMENT')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addComment(
            @RequestParam(name = AppConstants.BLOG_POST_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String blogPostId,
            @Valid @RequestBody CommentRequest request) {
        return commentService.addComment(blogPostId, request);
    }

    // ========================= GET COMMENTS =========================
    @PreAuthorize("hasAuthority('COMMENT_MANAGEMENT.VIEW_COMMENT')")
    @GetMapping("/get")
    public ResponseEntity<List<CommentResponse>> getComments(
            @RequestParam(name = AppConstants.BLOG_POST_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String blogPostId) {
        return commentService.getCommentsByBlogPostId(blogPostId);
    }

    // ========================= DELETE COMMENT =========================
    @PreAuthorize("hasAuthority('COMMENT_MANAGEMENT.DELETE_COMMENT')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteComment(
            @RequestParam(name = AppConstants.COMMENT_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String commentId) {
        return commentService.deleteComment(commentId);
    }

    // ========================= LIKE / UNLIKE COMMENT =========================
    @PreAuthorize("hasAuthority('COMMENT_MANAGEMENT.LIKE_COMMENT')")
    @PutMapping("/like")
    public ResponseEntity<ApiResponse> likeOrUnlikeComment(
            @RequestParam(name = AppConstants.COMMENT_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String commentId,
            @RequestParam(name = AppConstants.IS_LIKE) Boolean isLike) {
        return commentService.likeComment(commentId, isLike);
    }

    // ========================= DELETE REPLY =========================
    @PreAuthorize("hasAuthority('COMMENT_MANAGEMENT.DELETE_COMMENT')")
    @DeleteMapping("/delete-reply")
    public ResponseEntity<ApiResponse> deleteReply(
            @RequestParam(name = AppConstants.REPLY_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String replyId) {
        return commentService.deleteReply(replyId);
    }
}
