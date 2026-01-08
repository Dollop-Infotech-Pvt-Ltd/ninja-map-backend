package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.ISearchHistoryService;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@Validated
public class SearchHistoryController {

	@Autowired
	private ISearchHistoryService searchHistoryService;

	/**
	 * Record a new search query
	 */
	@PostMapping("/record")
	public ResponseEntity<ApiResponse> recordSearch(@Valid @RequestBody SearchHistoryRequest searchHistoryRequest) {
		ApiResponse response = searchHistoryService.recordSearch(searchHistoryRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * Get paginated search history for the authenticated user
	 */
	@GetMapping("/get-search-history")
	public ResponseEntity<ApiResponse> getSearchHistory(
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.searchValue(searchValue)
				.build();

		ApiResponse response = searchHistoryService.getSearchHistory(paginationRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * Get recent searches with limit
	 */
	@GetMapping("/recent")
	public ResponseEntity<ApiResponse> getRecentSearches(
			@RequestParam(name = "limit", defaultValue = "10", required = false) Integer limit) {

		ApiResponse response = searchHistoryService.getRecentSearches(limit);
		return ResponseEntity.ok(response);
	}

	/**
	 * Get search history filtered by search type
	 */
	@GetMapping("/by-type")
	public ResponseEntity<ApiResponse> getSearchHistoryByType(
			@RequestParam(name = "searchType") String searchType,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
			@RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.searchValue(searchValue)
				.build();

		ApiResponse response = searchHistoryService.getSearchHistoryByType(searchType, paginationRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * Delete a specific search entry by ID
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteSearchEntry(@RequestParam String id) {
		ApiResponse response = searchHistoryService.deleteSearchEntry(id);
		return ResponseEntity.ok(response);
	}

	/**
	 * Clear all search history for the authenticated user
	 */
	@DeleteMapping("/clear-search")
	public ResponseEntity<ApiResponse> clearAllSearchHistory() {
		ApiResponse response = searchHistoryService.clearAllSearchHistory();
		return ResponseEntity.ok(response);
	}
}
