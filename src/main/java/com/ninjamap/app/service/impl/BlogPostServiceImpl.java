package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.ArticleStats;
import com.ninjamap.app.model.BlogPost;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.BlogPostRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.ArticleStatsResponse;
import com.ninjamap.app.payload.response.AuthorResponse;
import com.ninjamap.app.payload.response.BlogDetailResponse;
import com.ninjamap.app.payload.response.BlogListItemResponse;
import com.ninjamap.app.payload.response.CommentResponse;
import com.ninjamap.app.payload.response.HomepageResponse;
import com.ninjamap.app.repository.IBlogPostRepository;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.IBlogPostService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.ICommentService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.transaction.Transactional;
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

	// ================= CREATE / UPDATE POST =================
	@Override
	public ResponseEntity<ApiResponse> createPost(BlogPostRequest request) {
		User userAuthor = null;
		Admin adminAuthor = null;
		String role = jwtUtils.extractRole(jwtUtils.extractTokenFromHeader());

		if ("USER".equals(role))
			userAuthor = getCurrentUser();
		else
			adminAuthor = getCurrentAdmin();

		BlogPost blogPost = BlogPost.builder().title(request.getTitle()).previewContent(request.getPreviewContent())
				.detailedContent(request.getDetailedContent()).category(request.getCategory())
				.isFeaturedArticle(Boolean.TRUE.equals(request.getIsFeaturedArticle()))
				.thumbnailUrl(uploadIfPresent(request.getThumbnailImage(), AppConstants.BLOG_THUMBNAILS))
				.featuredImageUrl(uploadIfPresent(request.getFeaturedImage(), AppConstants.BLOG_POSTS))
				.userAuthor(userAuthor).adminAuthor(adminAuthor)
				.readTimeMinutes(calculateReadTime(request.getDetailedContent())).tags(request.getTags())
				.stats(initStats()).build();

		return saveAndRespond(blogPost, AppConstants.BLOG_POST_CREATED, AppConstants.BLOG_POST_NOT_CREATED);
	}

	@Override
	public ResponseEntity<ApiResponse> updateBlogPost(String id, BlogPostRequest request) {
		BlogPost existing = findPost(id);

		// ===== Ownership Validation =====
		if (!isOwnerOfPost(existing)) {
			throw new UnauthorizedException(AppConstants.BLOG_POST_UNAUTHORIZED_UPDATE);
		}

		existing.setTitle(request.getTitle());
		existing.setPreviewContent(request.getPreviewContent());
		existing.setDetailedContent(request.getDetailedContent());
		existing.setCategory(request.getCategory());
		existing.setReadTimeMinutes(calculateReadTime(request.getDetailedContent()));
		existing.setTags(request.getTags() != null ? request.getTags() : existing.getTags());

		if (request.getThumbnailImage() != null && !request.getThumbnailImage().isEmpty())
			existing.setThumbnailUrl(uploadIfPresent(request.getThumbnailImage(), AppConstants.BLOG_THUMBNAILS));

		if (request.getFeaturedImage() != null && !request.getFeaturedImage().isEmpty())
			existing.setFeaturedImageUrl(uploadIfPresent(request.getFeaturedImage(), AppConstants.BLOG_POSTS));

		return saveAndRespond(existing, AppConstants.BLOG_POST_UPDATED, AppConstants.BLOG_POST_NOT_UPDATED);
	}

	private boolean isOwnerOfPost(BlogPost post) {
		String role = jwtUtils.extractRole(jwtUtils.extractTokenFromHeader());
		String loggedInId;

		if ("USER".equalsIgnoreCase(role)) {
			loggedInId = getCurrentUser().getUserId();
		} else if ("ADMIN".equalsIgnoreCase(role)) {
			loggedInId = getCurrentAdmin().getAdminId();
		} else {
			return false; // Unknown or unauthenticated role
		}

		String authorId = null;
		if (post.getUserAuthor() != null) {
			authorId = post.getUserAuthor().getUserId();
		} else if (post.getAdminAuthor() != null) {
			authorId = post.getAdminAuthor().getAdminId();
		}

		// ===== Final Ownership Check =====
		return authorId != null && authorId.equals(loggedInId);
	}

	@Override
	public ResponseEntity<ApiResponse> deleteBlogPost(String id) {
		BlogPost post = findPost(id);
		post.setIsActive(false);
		post.setIsDeleted(true);
		return saveAndRespond(post, AppConstants.BLOG_POST_DELETED, AppConstants.BLOG_POST_NOT_DELETED);
	}

	@Override
	public ResponseEntity<ApiResponse> getPostById(String id) {
		BlogPost post = findPost(id);
		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(AppConstants.BLOG_POST_FETCHED, mapToDetailResponse(post)));
	}

	@Override
	public ResponseEntity<ApiResponse> getHomepagePosts(BlogCategory category, PaginationRequest paginationRequest) {
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, BlogPost.class);

		List<BlogPost> featuredPosts = blogPostRepository.findTopFeaturedByCategory(category, pageable);
		List<String> featuredIds = featuredPosts.stream().map(BlogPost::getId).toList();
		List<BlogPost> latestPosts = blogPostRepository.findTopLatestByCategory(category, featuredIds, pageable);

		HomepageResponse response = HomepageResponse.builder()
				.featuredArticles(featuredPosts.stream().map(this::mapToListItem).toList())
				.latestArticles(latestPosts.stream().map(this::mapToListItem).toList())
				.totalFeatured(featuredPosts.size()).totalLatest(latestPosts.size()).build();

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.HOMEPAGE_POSTS_FETCHED, response));
	}

	// ================= ENGAGEMENT =================
	@Override
	public ResponseEntity<ApiResponse> toggleLike(String postId, Boolean like) {
		return updateUserSet(postId, like, "like");
	}

	@Override
	public ResponseEntity<ApiResponse> toggleSave(String postId, Boolean save) {
		return updateUserSet(postId, save, "save");
	}

	@Override
	public ResponseEntity<ApiResponse> sharePost(String postId) {
		BlogPost post = findPost(postId);
		User currentUser = getCurrentUser();

		if (post.getSharedByUsers().add(currentUser))
			incrementStats(post, "shares");

		return saveAndRespond(post, AppConstants.BLOG_POST_SHARED, null);
	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponse> addView(String postId) {
		BlogPost post = findPost(postId);
		User currentUser = getCurrentUser();

		if (!post.getViewedByUsers().contains(currentUser)) {
			post.getViewedByUsers().add(currentUser);
			incrementStats(post, "views");
		}

		return saveAndRespond(post, AppConstants.BLOG_POST_VIEWED, null);
	}

	// ================= HELPERS =================
	private BlogPost findPost(String id) {
		return blogPostRepository.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BLOG_POST_NOT_FOUND));
	}

	private User getCurrentUser() {
		return userService.getUserByEmailAndIsActive(userService.getCurrectUserFromToken().getEmail(), null);
	}

	private Admin getCurrentAdmin() {
		return adminService.getAdminByEmailAndIsActive(adminService.getCurrectAdminFromToken().getEmail(), null);
	}

	private String uploadIfPresent(MultipartFile file, String folder) {
		return (file != null && !file.isEmpty()) ? cloudinaryService.uploadFile(file, folder) : null;
	}

	private ArticleStats initStats() {
		return ArticleStats.builder().views(0).likes(0).comments(0).shares(0).build();
	}

	private int calculateReadTime(String content) {
		if (content == null || content.isEmpty())
			return 0;
		int words = content.split("\\s+").length;
		return Math.max(1, words / 200);
	}

	private void incrementStats(BlogPost post, String field) {
		if (post.getStats() == null)
			post.setStats(initStats());
		switch (field) {
		case "views" -> post.getStats().setViews(post.getStats().getViews() + 1);
		case "likes" -> post.getStats().setLikes(post.getStats().getLikes() + 1);
		case "shares" -> post.getStats().setShares(post.getStats().getShares() + 1);
		}
	}

	private ResponseEntity<ApiResponse> updateUserSet(String postId, Boolean flag, String type) {
		BlogPost post = findPost(postId);
		User user = getCurrentUser();

		switch (type) {
		case "like" -> {
			if (flag && post.getLikedByUsers().add(user))
				incrementStats(post, "likes");
			if (!flag && post.getLikedByUsers().remove(user))
				post.getStats().setLikes(Math.max(0, post.getStats().getLikes() - 1));
		}
		case "save" -> {
			if (flag)
				post.getSavedByUsers().add(user);
			else
				post.getSavedByUsers().remove(user);
		}
		}

		String msg = switch (type) {
		case "like" -> flag ? AppConstants.BLOG_POST_LIKED : AppConstants.BLOG_POST_UNLIKED;
		case "save" -> flag ? AppConstants.BLOG_POST_SAVED : AppConstants.BLOG_POST_UNSAVED;
		default -> "";
		};

		return saveAndRespond(post, msg, null);
	}

	private ResponseEntity<ApiResponse> saveAndRespond(BlogPost post, String successMsg, String failureMsg) {
		try {
			blogPostRepository.save(post);
			return ResponseEntity.ok(AppUtils.buildSuccessResponse(successMsg));
		} catch (Exception e) {
			return ResponseEntity.badRequest()
					.body(AppUtils.buildFailureResponse(failureMsg != null ? failureMsg : "Operation failed"));
		}
	}

	// ================= MAPPING =================
	private BlogListItemResponse mapToListItem(BlogPost post) {
		return BlogListItemResponse.builder().id(post.getId()).title(post.getTitle())
				.previewContent(post.getPreviewContent()).category(post.getCategory())
				.readTimeMinutes(post.getReadTimeMinutes()).thumbnailUrl(post.getThumbnailUrl())
				.postDate(post.getCreatedDate()).views(post.getStats() != null ? post.getStats().getViews() : 0)
				.likes(post.getStats() != null ? post.getStats().getLikes() : 0).author(mapToAuthorResponse(post))
				.build();
	}

	private AuthorResponse mapToAuthorResponse(BlogPost post) {
		return AuthorResponse.builder().name(post.getAuthorName()).designation(post.getAuthorRole())
				.profilePicture(post.getAuthorProfile()).bio(post.getAuthorBio()).build();
	}

	private BlogDetailResponse mapToDetailResponse(BlogPost post) {
		AuthorResponse author = mapToAuthorResponse(post);
		ArticleStatsResponse stats = post.getStats() != null
				? ArticleStatsResponse.builder().views(post.getStats().getViews()).likes(post.getStats().getLikes())
						.comments(post.getStats().getComments()).shares(post.getStats().getShares()).build()
				: null;

		List<CommentResponse> comments = post.getComments().stream()
				.filter(c -> c.getParentComment() == null && !Boolean.TRUE.equals(c.getIsDeleted()))
				.map(commentService::mapToCommentRespons).collect(Collectors.toList());

		List<BlogListItemResponse> relatedArticles = blogPostRepository
				.findTop3ByCategoryAndIdNotAndIsDeletedFalseOrderByCreatedDateDesc(post.getCategory(), post.getId())
				.stream().map(this::mapToListItem).collect(Collectors.toList());

		String role = jwtUtils.extractRole(jwtUtils.extractTokenFromHeader());
		User currentUser = null;
//		    Admin currentAdmin = null;

		if ("USER".equalsIgnoreCase(role)) {
			currentUser = getCurrentUser();
		}
//		    else if ("ADMIN".equalsIgnoreCase(role)) {
//		        currentAdmin = getCurrentAdmin();
//		    }

		// ===== Engagement Flags =====
		Boolean isLike = false;
		Boolean isSave = false;

		if (currentUser != null) {
			isLike = post.getLikedByUsers().contains(currentUser);
			isSave = post.getSavedByUsers().contains(currentUser);
		}

//		    if (currentAdmin != null) {
//		        isLike = post.getLikedByUsers().contains(currentAdmin);
//		        isSave = post.getSavedByUsers().contains(currentAdmin);
//		    }

		return BlogDetailResponse.builder().id(post.getId()).title(post.getTitle()).category(post.getCategory())
				.previewContent(post.getPreviewContent()).detailedContent(post.getDetailedContent())
				.featuredImageUrl(post.getFeaturedImageUrl()).readTimeMinutes(post.getReadTimeMinutes())
				.postDate(post.getCreatedDate()).author(author).stats(stats).tags(post.getTags()).comments(comments)
				.relatedArticles(relatedArticles).isLike(isLike).isSave(isSave).build();
	}
}
