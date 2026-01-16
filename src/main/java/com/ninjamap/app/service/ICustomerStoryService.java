package com.ninjamap.app.service;
import org.springframework.http.ResponseEntity;
import com.ninjamap.app.payload.request.CreateCustomerStoryRequest;
import com.ninjamap.app.payload.request.PaginationRequest;

public interface ICustomerStoryService {

	ResponseEntity<?> createStory(CreateCustomerStoryRequest request);
	
	ResponseEntity<?> getStoryById(String storyId);

	ResponseEntity<?> getAllStories(PaginationRequest paginationRequest,String category);


	ResponseEntity<?> likeStory(String storyId);

	ResponseEntity<?> unlikeStory(String storyId);

	ResponseEntity<?> addView(String storyId);

	ResponseEntity<?> getCustomerStories(PaginationRequest paginationRequest,String status);

	ResponseEntity<?> approveStory(String storyId,Boolean isApproved);
}
