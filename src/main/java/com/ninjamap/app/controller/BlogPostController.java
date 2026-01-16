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

    // ========================= CREATE BLOG =========================
    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.CREATE_BLOGS')")
    @PostMapping("/post")
    public ResponseEntity<ApiResponse> createBlogPost(@Valid BlogPostRequest request) {
        return blogPostService.createPost(request);
    }

    // ========================= UPDATE BLOG =========================
    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.EDIT_BLOGS')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
            @Valid BlogPostRequest request) {
        return blogPostService.updateBlogPost(id, request);
    }

    // ========================= DELETE BLOG =========================
    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.DELETE_BLOGS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return blogPostService.deleteBlogPost(id);
    }

    // ========================= GET BLOG BY ID =========================
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getBlogPostById(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return blogPostService.getPostById(id);
    }

    // ========================= GET ALL BLOGS =========================
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse> getAllBlogPostsForHomepage(
            @RequestParam(required = false) BlogCategory category,
            @RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
            @RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .sortDirection(sortDirection)
                .sortKey(sortKey)
                .build();

        return blogPostService.getHomepagePosts(category, paginationRequest);
    }
    
    // ========================= GET ALL BLOGS =========================
    @GetMapping("/get-all-blogs")
    public ResponseEntity<ApiResponse> getAllBlogPostsForAdmin(
            @RequestParam(required = false) BlogCategory category,
            @RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
            @RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .sortDirection(sortDirection)
                .sortKey(sortKey)
                .build();

        return blogPostService.getHomepagePosts(category, paginationRequest);
    }

    // ========================= ENGAGEMENT ENDPOINTS =========================
    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.LIKE_BLOGS')")
    @PutMapping("/like")
    public ResponseEntity<ApiResponse> likeOrUnlikeBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String postId,
            @RequestParam(name = AppConstants.IS_LIKE) Boolean isLike) {
        return blogPostService.toggleLike(postId, isLike);
    }

    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.SAVE_BLOGS')")
    @PutMapping("/save")
    public ResponseEntity<ApiResponse> saveOrUnsaveBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String postId,
            @RequestParam(name = AppConstants.IS_SAVE) Boolean isSave) {
        return blogPostService.toggleSave(postId, isSave);
    }

    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.SHARE_BLOGS')")
    @PutMapping("/share")
    public ResponseEntity<ApiResponse> shareBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String postId) {
        return blogPostService.sharePost(postId);
    }

    @PreAuthorize("hasAuthority('BLOG_POST_MANAGEMENT.VIEW_BLOGS')")
    @PutMapping("/view")
    public ResponseEntity<ApiResponse> viewBlogPost(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String postId) {
        return blogPostService.addView(postId);
    }
}
