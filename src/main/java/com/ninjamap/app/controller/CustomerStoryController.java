package com.ninjamap.app.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.enums.StoryCategory;
import com.ninjamap.app.payload.request.CreateCustomerStoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.service.ICustomerStoryService;
import com.ninjamap.app.utils.constants.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer-stories")
@RequiredArgsConstructor
@Validated
public class CustomerStoryController {

	private final ICustomerStoryService customerStoryService;

	// ========================= CREATE STORY =========================
//	@PreAuthorize("hasAuthority('CUSTOMER_STORIES.CREATE')")
	@PostMapping("/create")
	public ResponseEntity<?> createStory(@Valid CreateCustomerStoryRequest request) {
		return customerStoryService.createStory(request);
	}

	// ========================= GET STORY BY ID =========================
	@GetMapping("/get")
	public ResponseEntity<?> getStoryById(
			@RequestParam(name = AppConstants.ID) String storyId) {
		return customerStoryService.getStoryById(storyId);
	}

	// ========================= GET ALL STORIES =========================
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllStories(
			@RequestParam(name = com.ninjamap.app.utils.constants.AppConstants.PAGE_SIZE,defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER,defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = "category", required = false,defaultValue = "All") String category,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.searchValue(searchValue)
				.build();

		return customerStoryService.getAllStories(paginationRequest,category);
	}

	// ========================= LIKE STORY =========================
	@PreAuthorize("hasAuthority('CUSTOMER_STORIES.LIKE')")
	@PostMapping("/like")
	public ResponseEntity<?> likeStory(
			@RequestParam(name = AppConstants.ID) String storyId) {
		return customerStoryService.likeStory(storyId);
	}

	// ========================= UNLIKE STORY =========================
	@PreAuthorize("hasAuthority('CUSTOMER_STORIES.LIKE')")
	@PostMapping("/unlike")
	public ResponseEntity<?> unlikeStory(
			@RequestParam(name = AppConstants.ID) String storyId) {
		return customerStoryService.unlikeStory(storyId);
	}

	// ========================= ADMIN: GET PENDING STORIES =========================
	@PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.VIEW_ADMINS')")
	@GetMapping("/admin/get-customer-stories")
	public ResponseEntity<?> getCustomerStories(
			@RequestParam(defaultValue = "APPROVE") String status,
			@RequestParam(name = AppConstants.PAGE_SIZE,defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER,defaultValue = "0") Integer pageNumber) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.build();

		return customerStoryService.getCustomerStories(paginationRequest,status);
	}


	// ========================= ADMIN: APPROVE STORY =========================
	@PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.EDIT_ADMINS')")
	@PatchMapping("/admin/approve-reject")
	public ResponseEntity<?> approveOrRejectStory(
			@RequestParam(name = AppConstants.ID) String storyId,@RequestParam(name = "isApproved") Boolean isApproved) {
		return customerStoryService.approveStory(storyId,isApproved);
	}
}
