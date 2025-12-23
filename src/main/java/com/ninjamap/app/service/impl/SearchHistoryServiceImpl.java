package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.SearchHistory;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.SearchHistoryListResponse;
import com.ninjamap.app.payload.response.SearchHistoryResponse;
import com.ninjamap.app.repository.ISearchHistoryRepository;
import com.ninjamap.app.service.ISearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryServiceImpl implements ISearchHistoryService {

	private final ISearchHistoryRepository searchHistoryRepository;

	@Override
	@Transactional
	public ApiResponse recordSearch(SearchHistoryRequest searchHistoryRequest) {
		try {
			String userId = getCurrentUserId();
			String searchTerm = searchHistoryRequest.getSearchTerm().trim();
			
			// Check for duplicate search within 5 minutes
			LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
			var duplicateSearch = searchHistoryRepository.findRecentDuplicateSearch(userId, searchTerm, fiveMinutesAgo);

			if (duplicateSearch.isPresent()) {
				// Update existing search entry timestamp
				SearchHistory existingSearch = duplicateSearch.get();
				existingSearch.setUpdatedDate(LocalDateTime.now());
				searchHistoryRepository.save(existingSearch);
				return ApiResponse.builder()
						.success(true)
						.message("Search recorded successfully")
						.data(mapToSearchHistoryResponse(existingSearch))
						.build();
			}

			// Create new search entry
			SearchHistory searchHistory = SearchHistory.builder()
					.userId(userId)
					.searchTerm(searchTerm)
					.build();

			searchHistoryRepository.save(searchHistory);

			return ApiResponse.builder()
					.success(true)
					.message("Search recorded successfully")
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.success(false)
					.message("Error recording search")
					.data(null)
					.build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getSearchHistory(PaginationRequest paginationRequest) {
		try {
			String userId = getCurrentUserId();
			Pageable pageable = PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize());

			Page<SearchHistory> searchHistoryPage = searchHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);

			List<SearchHistoryResponse> responses = searchHistoryPage.getContent()
					.stream()
					.map(this::mapToSearchHistoryResponse)
					.collect(Collectors.toList());

			SearchHistoryListResponse listResponse = SearchHistoryListResponse.builder()
					.searchHistories(responses)
					.totalCount((int) searchHistoryPage.getTotalElements())
					.pageSize(paginationRequest.getPageSize())
					.pageNumber(paginationRequest.getPageNumber())
					.build();

			return ApiResponse.builder()
					.success(true)
					.message("Search history retrieved successfully")
					.data(listResponse)
					.build();

		} catch (Exception e) {
			log.error("Error retrieving search history", e);
			return ApiResponse.builder()
					.success(false)
					.message("Error retrieving search history")
					.data(null)
					.build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getRecentSearches(Integer limit) {
		try {
			String userId = getCurrentUserId();
			int searchLimit = limit != null && limit > 0 ? limit : 10;

			Pageable pageable = PageRequest.of(0, searchLimit);
			Page<SearchHistory> recentSearchesPage = searchHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);

			List<SearchHistoryResponse> responses = recentSearchesPage.getContent().stream()
					.map(this::mapToSearchHistoryResponse)
					.collect(Collectors.toList());

			return ApiResponse.builder()
					.success(true)
					.message("Recent searches retrieved successfully")
					.data(responses)
					.build();

		} catch (Exception e) {
			log.error("Error retrieving recent searches", e);
			return ApiResponse.builder()
					.success(false)
					.message("Error retrieving recent searches")
					.data(null)
					.build();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getSearchHistoryByType(String searchType, PaginationRequest paginationRequest) {
//		try {
//			String userId = getCurrentUserId();
//
//			// Validate search type
//			SearchType type;
//			try {
//				type = SearchType.valueOf(searchType.toUpperCase());
//			} catch (IllegalArgumentException e) {
//				return ApiResponse.builder()
//						.success(true)
//						.message("Invalid search type")
//						.data(SearchHistoryListResponse.builder()
//								.searchHistories(List.of())
//								.totalCount(0)
//								.pageSize(paginationRequest.getPageSize())
//								.pageNumber(paginationRequest.getPageNumber())
//								.build())
//						.build();
//			}
//
//			Pageable pageable = PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize());
//			Page<SearchHistory> searchHistoryPage = searchHistoryRepository.findByUserIdAndSearchTypeOrderByCreatedDateDesc(userId, type, pageable);
//
//			List<SearchHistoryResponse> responses = searchHistoryPage.getContent()
//					.stream()
//					.map(this::mapToSearchHistoryResponse)
//					.collect(Collectors.toList());
//
//			SearchHistoryListResponse listResponse = SearchHistoryListResponse.builder()
//					.searchHistories(responses)
//					.totalCount((int) searchHistoryPage.getTotalElements())
//					.pageSize(paginationRequest.getPageSize())
//					.pageNumber(paginationRequest.getPageNumber())
//					.build();
//
//			return ApiResponse.builder()
//					.success(true)
//					.message("Search history retrieved successfully")
//					.data(listResponse)
//					.build();
//
//		} catch (Exception e) {
//			log.error("Error retrieving search history by type", e);
//			return ApiResponse.builder()
//					.success(false)
//					.message("Error retrieving search history by type")
//					.data(null)
//					.build();
//		}
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse deleteSearchEntry(String searchId) {
		try {
			String userId = getCurrentUserId();

			SearchHistory searchHistory = searchHistoryRepository.findById(searchId)
					.orElseThrow(() -> new ResourceNotFoundException("Search entry not found"));

			// Verify ownership
			if (!searchHistory.getUserId().equals(userId)) {
				return ApiResponse.builder()
						.success(false)
						.message("Unauthorized to delete this search entry")
						.data(null)
						.build();
			}

			searchHistoryRepository.delete(searchHistory);
			log.info("Deleted search entry: {} for user: {}", searchId, userId);

			return ApiResponse.builder()
					.success(true)
					.message("Search entry deleted successfully")
					.data(null)
					.build();

		} catch (ResourceNotFoundException e) {
			return ApiResponse.builder()
					.success(false)
					.message("Search entry not found")
					.data(null)
					.build();
		} catch (Exception e) {
			log.error("Error deleting search entry", e);
			return ApiResponse.builder()
					.success(false)
					.message("Error deleting search entry")
					.data(null)
					.build();
		}
	}

	@Override
	@Transactional
	public ApiResponse clearAllSearchHistory() {
		try {
			String userId = getCurrentUserId();
			searchHistoryRepository.deleteByUserId(userId);
			log.info("Cleared all search history for user: {}", userId);

			return ApiResponse.builder()
					.success(true)
					.message("All search history cleared successfully")
					.data(null)
					.build();

		} catch (Exception e) {
			log.error("Error clearing search history", e);
			return ApiResponse.builder()
					.success(false)
					.message("Error clearing search history")
					.data(null)
					.build();
		}
	}

	/**
	 * Helper method to convert SearchHistory entity to SearchHistoryResponse
	 */
	private SearchHistoryResponse mapToSearchHistoryResponse(SearchHistory searchHistory) {
		return SearchHistoryResponse.builder()
				.id(searchHistory.getId())
				.searchTerm(searchHistory.getSearchTerm())
				.createdDate(searchHistory.getCreatedDate())
				.build();
	}

	/**
	 * Helper method to get current authenticated user ID
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}
