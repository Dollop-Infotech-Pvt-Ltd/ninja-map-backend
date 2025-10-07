package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.ArticleStats;
import com.ninjamap.app.model.BlogPost;
import com.ninjamap.app.payload.request.BlogPostRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BlogPostResponse;
import com.ninjamap.app.payload.response.CommentResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.IBlogPostRepository;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.IBlogPostService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.ICommentService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements IBlogPostService {

	private final IBlogPostRepository blogPostRepository;
	private final ICloudinaryService cloudinaryService;
	private final IUserService userService;
	private final IAdminService adminService;
	private final JwtUtils jwtUtils;
	private final ICommentService commentService;

	@Override
	public ResponseEntity<ApiResponse> createPost(BlogPostRequest request) {
		String role = jwtUtils.extractRole(jwtUtils.extractTokenFromHeader());

		String authorId = "USER".equals(role) ? userService.getCurrectUserFromToken().getId()
				: adminService.getCurrectAdminFromToken().getId();

		// Handle featured image upload via Cloudinary
		MultipartFile file = request.getFeaturedImage();
		String featuredImageUrl = (file != null && !file.isEmpty()) ? cloudinaryService.uploadFile(file, "Blog_Posts")
				: null;

		// Attach default stats
		ArticleStats stats = ArticleStats.builder().views(0).likes(0).comments(0).shares(0).build();

		BlogPost blogPost = BlogPost.builder().title(request.getTitle()).content(request.getContent())
				.category(request.getCategory()).authorId(authorId).authorRole(role).readTimeMinutes(0)
				.featuredImageUrl(featuredImageUrl).tags(request.getTags()).stats(stats) // ✅ attach stats
				.build();

		BlogPost saved = blogPostRepository.save(blogPost);

		// Build response based on save result
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.BLOG_POST_CREATED)
				: AppUtils.buildFailureResponse(AppConstants.BLOG_POST_NOT_CREATED);

		return new ResponseEntity<>(response, response.getHttp());

	}

	@Override
	public ResponseEntity<ApiResponse> updateBlogPost(String id, BlogPostRequest request) {
		BlogPost existing = findById(id);

		// Update fields
		existing.setTitle(request.getTitle());
		existing.setContent(request.getContent());
		existing.setCategory(request.getCategory());

		if (request.getFeaturedImage() != null && !request.getFeaturedImage().isEmpty()) {
			String featuredImageUrl = cloudinaryService.uploadFile(request.getFeaturedImage(), "Blog_Posts");
			existing.setFeaturedImageUrl(featuredImageUrl);
		}

		existing.setTags(request.getTags() != null ? request.getTags() : existing.getTags());

		BlogPost saved = blogPostRepository.save(existing);

		// Build response based on save result
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.BLOG_POST_UPDATED)
				: AppUtils.buildFailureResponse(AppConstants.BLOG_POST_NOT_UPDATED);
		return new ResponseEntity<>(response, response.getHttp());
	}

	private BlogPost findById(String id) {
		return blogPostRepository.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));
	}

	@Override
	public ResponseEntity<ApiResponse> deleteBlogPost(String id) {
		BlogPost blogPost = findById(id);

		// Soft delete
		blogPost.setIsActive(false);
		blogPost.setIsDeleted(true);

		BlogPost saved = blogPostRepository.save(blogPost);

		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.BLOG_POST_DELETED)
				: AppUtils.buildFailureResponse(AppConstants.BLOG_POST_NOT_DELETED);
		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> getPostById(String id) {
		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(AppConstants.BLOG_POST_FETCHED, mapToResponse(findById(id))));
	}

	@Override
	public ResponseEntity<PaginatedResponse<BlogPostResponse>> getAllPosts(BlogCategory category,
			PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, BlogPost.class);
		Page<BlogPost> page = blogPostRepository.findByCategoryOptional(category, pageable);
		Page<BlogPostResponse> responsePage = page.map(this::mapToResponse);
		PaginatedResponse<BlogPostResponse> paginatedResponse = new PaginatedResponse<>(responsePage);

		return ResponseEntity.ok(paginatedResponse);

	}

	private BlogPostResponse mapToResponse(BlogPost blogPost) {
		if (blogPost == null)
			return null;

		String authorName = "USER".equals(blogPost.getAuthorRole())
				? userService.getCurrectUserFromToken().getFullName()
				: adminService.getCurrectAdminFromToken().getFullName();

		List<CommentResponse> commentResponses = blogPost.getComments().stream()
				.map(commentService::mapToCommentRespons).collect(Collectors.toList());

		return BlogPostResponse.builder().id(blogPost.getId()).title(blogPost.getTitle()).content(blogPost.getContent())
				.category(blogPost.getCategory()).featuredImageUrl(blogPost.getFeaturedImageUrl())
				.publishedDate(blogPost.getCreatedDate()).readTimeMinutes(blogPost.getReadTimeMinutes())
				.views(blogPost.getStats() != null ? blogPost.getStats().getViews() : 0)
				.likes(blogPost.getStats() != null ? blogPost.getStats().getLikes() : 0)
				.comments(blogPost.getStats() != null ? blogPost.getStats().getComments() : 0)
				.shares(blogPost.getStats() != null ? blogPost.getStats().getShares() : 0).authorName(authorName)
				.authorRole(blogPost.getAuthorRole()).tags(blogPost.getTags()).commentsList(commentResponses).build();
	}
}
