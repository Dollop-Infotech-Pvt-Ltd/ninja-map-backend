package com.ninjamap.app.service.impl;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ninjamap.app.enums.StoryCategory;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.ArticleStats;
import com.ninjamap.app.model.CustomerStory;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.request.CreateCustomerStoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.ArticleStatsResponse;
import com.ninjamap.app.payload.response.AuthorResponse;
import com.ninjamap.app.payload.response.CustomerStoryDetailResponse;
import com.ninjamap.app.payload.response.CustomerStoryResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.ICustomerStoryRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.utils.constants.AppConstants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerStoryServiceImpl implements com.ninjamap.app.service.ICustomerStoryService {

	private final ICustomerStoryRepository storyRepository;
	private final IUserRepository userRepository;
	private final ICloudinaryService cloudinaryUtils;

	@Override
	public ResponseEntity<?> createStory(CreateCustomerStoryRequest request) {

		
	
		CustomerStory story = CustomerStory.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.category(request.getCategory())
				.authorEmail(request.getAuthorEmail())
				.authorName(request.getAuthorName())
				.rating(0.0)
				.location(request.getLocation())
				.authorBio(request.getAuthorBio())
				.stats(ArticleStats.builder().likes(0).build())
				.build();
		
		if(request.getAuthorProfilePic()!=null) {
			String profile = this.cloudinaryUtils.uploadFile(request.getAuthorProfilePic(), "cutomer-story-user-profile");
			story.setAuthorProfilePicture(profile);
		}
		

		storyRepository.save(story);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.builder()
						.message("Story submitted successfully. Pending admin approval.")
						.build());
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> getStoryById(String storyId) {
		User currentUser = getCurrentUser();
		CustomerStory story = findStory(storyId);

		addView(storyId);

		return ResponseEntity.ok(ApiResponse.builder()
				.message(AppConstants.STORY_FETCHED_SUCCESSFULLY)
				.data(mapToDetailResponse(story, currentUser))
				.build());
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> getAllStories(PaginationRequest paginationRequest,String category) {

		Pageable pageable = PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize());

		Page<CustomerStory> storiesPage = null;
	     if(category.equals("All")){
		storiesPage = storyRepository.findByIsDeletedFalseAndIsActiveTrueAndIsApprovedTrueOrderByCreatedDateDesc(pageable);
		}
	    else {
	    	StoryCategory storyCategory = StoryCategory.valueOf(category.toUpperCase());
	    	storiesPage = storyRepository.findByIsDeletedFalseAndIsActiveTrueAndCategoryOrderByCreatedDateDesc(storyCategory, pageable);
		}
		List<CustomerStoryResponse> responses = storiesPage.getContent()
				.stream()
				.map(story -> mapToResponse(story))
				.collect(Collectors.toList());

		return ResponseEntity.ok(PaginatedResponse.<CustomerStoryResponse>builder()
				.content(responses)
				.pageNumber(paginationRequest.getPageNumber())
				.pageSize(paginationRequest.getPageSize())
				.totalElements((int) storiesPage.getTotalElements())
				.totalPages(storiesPage.getTotalPages())
				.build());
	}

	@Override
	public ResponseEntity<?> likeStory(String storyId) {
		User currentUser = getCurrentUser();
		CustomerStory story = findStory(storyId);

		if (!story.getLikedByUsers().contains(currentUser)) {
			story.getLikedByUsers().add(currentUser);
			incrementStats(story, "likes");
		}

		return saveAndRespond(story, AppConstants.STORY_LIKED);
	}

	@Override
	public ResponseEntity<?> unlikeStory(String storyId) {
		User currentUser = getCurrentUser();
		CustomerStory story = findStory(storyId);

		if (story.getLikedByUsers().contains(currentUser)) {
			story.getLikedByUsers().remove(currentUser);
			decrementStats(story, "likes");
		}

		return saveAndRespond(story, AppConstants.STORY_UNLIKED);
	}

	@Override
	public ResponseEntity<?> addView(String storyId) {
		CustomerStory story = findStory(storyId);
		User currentUser = getCurrentUser();

		storyRepository.save(story);
		return ResponseEntity.ok(ApiResponse.builder()
				.message(AppConstants.STORY_VIEWED)
				.build());
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<?> getCustomerStories(PaginationRequest paginationRequest,String status) {
		Pageable pageable = PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize());
		Page<CustomerStory> storiesPage = null;
		if(status.equals("PENDING")) {
			storiesPage = storyRepository.findPendingStories(pageable);
		}else {
			storiesPage = storyRepository.findApprovedStories(pageable);
		}
		List<CustomerStoryResponse> responses = storiesPage.getContent()
				.stream()
				.map(story -> mapToResponse(story))
				.collect(Collectors.toList());

		return ResponseEntity.ok(PaginatedResponse.<CustomerStoryResponse>builder()
				.content(responses)
				.pageNumber(paginationRequest.getPageNumber())
				.pageSize(paginationRequest.getPageSize())
				.totalElements((int) storiesPage.getTotalElements())
				.totalPages(storiesPage.getTotalPages())
				.build());
	}

	@Override
	public ResponseEntity<?> approveStory(String storyId,Boolean isApproved) {
		CustomerStory story = findStory(storyId);

		story.setIsApproved(isApproved);
		storyRepository.save(story);

		return ResponseEntity.ok(ApiResponse.builder()
				.message("Story status updated sucessfully")
				.build());
	}

	private CustomerStory findStory(String storyId) {
		return storyRepository.findByIdAndIsDeletedFalse(storyId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.STORY_NOT_FOUND));
	}

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return userRepository.findByPersonalInfo_Email(email)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
	}

	private void incrementStats(CustomerStory story, String statType) {
		ArticleStats stats = story.getStats();
	  if ("likes".equals(statType)) {
			stats.setLikes(stats.getLikes() + 1);
		} 
	}

	private void decrementStats(CustomerStory story, String statType) {
		ArticleStats stats = story.getStats();
		if ("likes".equals(statType) && stats.getLikes() > 0) {
			stats.setLikes(stats.getLikes() - 1);
		}
	}

	private ResponseEntity<?> saveAndRespond(CustomerStory story, String message) {
		storyRepository.save(story);
		return ResponseEntity.ok(ApiResponse.builder()
				.message(message)
				.data(mapToResponse(story))
				.build());
	}

	private CustomerStoryResponse mapToResponse(CustomerStory story) {
		return CustomerStoryResponse.builder()
				.id(story.getId())
				.title(story.getTitle())
				.description(story.getDescription())
				.category(story.getCategory())
				.rating(story.getRating())
				.location(story.getLocation())
				.author(mapToAuthorResponse(story))
				.stats(mapToStatsResponse(story.getStats()))
				.createdDate(story.getCreatedDate())
				.updatedDate(story.getUpdatedDate())
				.isActive(story.getIsActive())
				.build();
	}

	private CustomerStoryDetailResponse mapToDetailResponse(CustomerStory story, User currentUser) {
		return CustomerStoryDetailResponse.builder()
				.id(story.getId())
				.title(story.getTitle())
				.description(story.getDescription())
				.category(story.getCategory())
				.rating(story.getRating())
				.location(story.getLocation())
				.author(mapToAuthorResponse(story))
				.stats(mapToStatsResponse(story.getStats()))
				.isLiked(currentUser != null && story.getLikedByUsers().contains(currentUser))
				.createdDate(story.getCreatedDate())
				.updatedDate(story.getUpdatedDate())
				.isActive(story.getIsActive())
				.build();
	}

	private AuthorResponse mapToAuthorResponse(CustomerStory story) {
		
			return AuthorResponse.builder()
					.name(story.getAuthorName())
					.email(story.getAuthorEmail())
					.bio(story.getAuthorBio())
					.organisationName(story.getOrganisationName())
					.profilePicture(story.getAuthorProfilePicture())
					.build();
	
	}

	private ArticleStatsResponse mapToStatsResponse(ArticleStats stats) {
		return ArticleStatsResponse.builder()
				.likes(stats.getLikes())
				.build();
	}
}
