package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface ISearchHistoryService {

	/**
	 * Record a new search query for the authenticated user
	 */
	ApiResponse recordSearch(SearchHistoryRequest searchHistoryRequest);

	/**
	 * Get paginated search history for the authenticated user
	 */
	ApiResponse getSearchHistory(PaginationRequest paginationRequest);

	/**
	 * Get recent searches with limit for the authenticated user
	 */
	ApiResponse getRecentSearches(Integer limit);

	/**
	 * Get search history filtered by search type for the authenticated user
	 */
	ApiResponse getSearchHistoryByType(String searchType, PaginationRequest paginationRequest);

	/**
	 * Delete a specific search entry by ID
	 */
	ApiResponse deleteSearchEntry(String searchId);

	/**
	 * Clear all search history for the authenticated user
	 */
	ApiResponse clearAllSearchHistory();
}
