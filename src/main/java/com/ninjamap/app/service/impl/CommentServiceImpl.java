package com.ninjamap.app.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.ArticleStats;
import com.ninjamap.app.model.BlogPost;
import com.ninjamap.app.model.Comment;
import com.ninjamap.app.payload.request.CommentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CommentResponse;
import com.ninjamap.app.repository.IBlogPostRepository;
import com.ninjamap.app.repository.ICommentRepository;
import com.ninjamap.app.service.ICommentService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

	private final ICommentRepository commentRepository;
	private final IBlogPostRepository blogPostRepository;

	@Override
	public ResponseEntity<ApiResponse> addComment(String blogPostId, CommentRequest request) {
		BlogPost blogPost = blogPostRepository.findByIdAndIsDeletedFalse(blogPostId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));

		Comment comment = Comment.builder().name(request.getName()).email(request.getEmail())
				.content(request.getContent()).blogPost(blogPost).build();

		Comment saved = commentRepository.save(comment);

		ArticleStats stats = blogPost.getStats();
		if (stats == null) {
			stats = ArticleStats.builder().views(0).likes(0).comments(1).shares(0).build();
			blogPost.setStats(stats);
		} else {
			stats.setComments(stats.getComments() + 1);
		}

		// Save updated stats via blogPost
		blogPostRepository.save(blogPost);

		// Build response
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.COMMENT_ADDED)
				: AppUtils.buildFailureResponse(AppConstants.COMMENT_NOT_ADDED);
		return new ResponseEntity<>(response, response.getHttp());
	}

//	@Override
//	public PaginatedResponse<CommentResponse> getCommentsByBlogPostId(String blogPostId) {
//		BlogPost blogPost = blogPostRepository.findById(blogPostId)
//				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));
//
//		Pageable pageable = AppUtils.buildPageableRequest(null, Comment.class); // or pass a pagination object
//		Page<Comment> page = commentRepository.findByBlogPostId(blogPost.getId(), pageable);
//
//		Page<CommentResponse> responsePage = page.map(this::mapToCommentRespons);
//		return new PaginatedResponse<>(responsePage);
//	}

	@Override
	public ResponseEntity<List<CommentResponse>> getCommentsByBlogPostId(String blogPostId) {
		// Fetch blog post
		BlogPost blogPost = blogPostRepository.findByIdAndIsDeletedFalse(blogPostId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));

		// Fetch all active comments for this blog post, latest first
		List<Comment> comments = commentRepository
				.findByBlogPostIdAndIsDeletedFalseOrderByCreatedDateDesc(blogPost.getId());

		// Map to CommentResponse DTO
		return ResponseEntity.ok(comments.stream().map(this::mapToCommentRespons).toList());
	}

	@Override
	public ResponseEntity<ApiResponse> deleteComment(String commentId) {
		Comment comment = findByIdCommentId(commentId);

		comment.setIsActive(false);
		comment.setIsDeleted(true);

		Comment saved = commentRepository.save(comment);

		BlogPost blogPost = comment.getBlogPost();
		if (blogPost.getStats() != null && blogPost.getStats().getComments() > 0) {
			blogPost.getStats().setComments(blogPost.getStats().getComments() - 1);
			blogPostRepository.save(blogPost); // persist updated stats
		}
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.COMMENT_DELETED)
				: AppUtils.buildFailureResponse(AppConstants.COMMENT_NOT_DELETED);
		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public CommentResponse mapToCommentRespons(Comment comment) {
		if (comment == null)
			return null;

		return CommentResponse.builder().id(comment.getId()).name(comment.getName()).email(comment.getEmail())
				.content(comment.getContent()).createdDate(comment.getCreatedDate()).build();
	}

	private Comment findByIdCommentId(String commentId) {
		return commentRepository.findByIdAndIsDeletedFalse(commentId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.COMMENT_NOT_FOUND));
	}
}
