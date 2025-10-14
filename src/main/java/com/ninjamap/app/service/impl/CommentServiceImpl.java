package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.ArticleStats;
import com.ninjamap.app.model.BlogPost;
import com.ninjamap.app.model.Comment;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.CommentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.CommentResponse;
import com.ninjamap.app.payload.response.UserResponse;
import com.ninjamap.app.repository.IBlogPostRepository;
import com.ninjamap.app.repository.ICommentRepository;
import com.ninjamap.app.service.ICommentService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final ICommentRepository commentRepository;
    private final IBlogPostRepository blogPostRepository;
    private final IUserService userService;

    @Override
    public ResponseEntity<ApiResponse> addComment(String blogPostId, CommentRequest request) {
        BlogPost blogPost = getBlogPost(blogPostId);
        User currentUser = getCurrentUserEntity();

        Comment comment = Comment.builder()
                .content(request.getContent())
                .blogPost(blogPost)
                .user(currentUser)
                .build();

        boolean isReply = false;

        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            Comment parent = getComment(request.getParentCommentId());
            comment.setParentComment(parent);
            isReply = true;
        }

        commentRepository.save(comment);

        if (!isReply) {
            updateArticleStats(blogPost, 1);
        }

        return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.COMMENT_ADDED));
    }

    @Override
    public ResponseEntity<List<CommentResponse>> getCommentsByBlogPostId(String blogPostId) {
        BlogPost blogPost = getBlogPost(blogPostId);

        List<Comment> comments = commentRepository
                .findByBlogPostIdAndIsDeletedFalseOrderByCreatedDateDesc(blogPost.getId());

        List<CommentResponse> response = comments.stream()
                .map(this::mapToCommentRespons)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteComment(String commentId) {
        Comment comment = getComment(commentId);
        User currentUser = getCurrentUserEntity();
        verifyOwnership(comment.getUser(), currentUser, AppConstants.COMMENT_DELETE_FORBIDDEN);

        softDeleteCommentAndReplies(comment);
        updateArticleStats(comment.getBlogPost(), -1);

        return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.COMMENT_DELETED));
    }

    @Override
    public ResponseEntity<ApiResponse> likeComment(String commentId, Boolean isLike) {
        Comment comment = getComment(commentId);
        User currentUser = getCurrentUserEntity();

        if (isLike) {
            comment.getLikedByUsers().add(currentUser);
        } else {
            comment.getLikedByUsers().remove(currentUser);
        }

        commentRepository.save(comment);

        return ResponseEntity.ok(AppUtils.buildSuccessResponse(
                isLike ? AppConstants.COMMENT_LIKED : AppConstants.COMMENT_UNLIKED
        ));
    }

    @Override
    public ResponseEntity<ApiResponse> deleteReply(String replyId) {
        Comment reply = getComment(replyId);

        if (reply.getParentComment() == null) {
            throw new ResourceNotFoundException(AppConstants.REPLY_NOT_FOUND);
        }

        User currentUser = getCurrentUserEntity();
        verifyOwnership(reply.getUser(), currentUser, AppConstants.REPLY_DELETE_FORBIDDEN);

        reply.setIsActive(false);
        reply.setIsDeleted(true);
        commentRepository.save(reply);

        // Replies do not affect ArticleStats
        return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.REPLY_DELETED));
    }

    @Override
    public CommentResponse mapToCommentRespons(Comment comment) {
        if (comment == null) return null;

        User user = comment.getUser();
        User currentUser = getCurrentUserEntity();

        boolean isLiked = comment.getLikedByUsers().contains(currentUser);

        List<CommentResponse> replies = comment.getReplies().stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .map(this::mapToCommentRespons)
                .collect(Collectors.toList());

        return CommentResponse.builder()
                .id(comment.getId())
                .name(user.getPersonalInfo().getFullName())
                .designation(user.getRole() != null ? user.getRole().getRoleName() : "User")
                .profilePicture(user.getPersonalInfo().getProfilePicture())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .likeCount(comment.getLikeCount())
                .isLike(isLiked)
                .replies(replies)
                .build();
    }

    // ---------------- PRIVATE METHODS ----------------

    private void softDeleteCommentAndReplies(Comment comment) {
        comment.setIsActive(false);
        comment.setIsDeleted(true);
        commentRepository.save(comment);

        comment.getReplies().stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .forEach(this::softDeleteCommentAndReplies);
    }

    private void updateArticleStats(BlogPost blogPost, int delta) {
        ArticleStats stats = blogPost.getStats();
        if (stats == null) {
            stats = ArticleStats.builder().views(0).likes(0).comments(Math.max(0, delta)).shares(0).build();
            blogPost.setStats(stats);
        } else {
            stats.setComments(Math.max(0, stats.getComments() + delta));
        }
        blogPostRepository.save(blogPost);
    }

    private BlogPost getBlogPost(String blogPostId) {
        return blogPostRepository.findByIdAndIsDeletedFalse(blogPostId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));
    }

    private Comment getComment(String commentId) {
        return commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.COMMENT_NOT_FOUND));
    }

    private void verifyOwnership(User resourceOwner, User currentUser, String message) {
        if (!resourceOwner.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenException(message);
        }
    }

    private User getCurrentUserEntity() {
        UserResponse currentUserResponse = userService.getCurrectUserFromToken();
        return userService.getUserByEmailAndIsActive(currentUserResponse.getEmail(), null);
    }
}
