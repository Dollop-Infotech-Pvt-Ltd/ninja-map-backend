package com.ninjamap.app.service.impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.model.SearchHistory;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.SearchHistoryListResponse;
import com.ninjamap.app.payload.response.SearchHistoryResponse;
import com.ninjamap.app.repository.ISearchHistoryRepository;
import com.ninjamap.app.service.ISearchHistoryService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.constants.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryServiceImpl implements ISearchHistoryService {

	private final ISearchHistoryRepository searchHistoryRepository;
	
	@Autowired
	private IUserService userService;

	@Override
	@Transactional
	public ApiResponse recordSearch(SearchHistoryRequest searchHistoryRequest) {

			   String userId = userService.getCurrectUserFromToken().getId();
			
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
						.message(AppConstants.SEARCH_RECORDED_SUCCESSFULLY)
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
					.message(AppConstants.SEARCH_RECORDED_SUCCESSFULLY)
					.build();
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getSearchHistory(PaginationRequest paginationRequest) {
		String userId = userService.getCurrectUserFromToken().getId();
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
					.message(AppConstants.SEARCH_FETCHED_SUCCESSFULLY)
					.data(listResponse)
					.build();
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getRecentSearches(Integer limit) {
		String userId = userService.getCurrectUserFromToken().getId();
			int searchLimit = limit != null && limit > 0 ? limit : 10;

			Pageable pageable = PageRequest.of(0, searchLimit);
			Page<SearchHistory> recentSearchesPage = searchHistoryRepository.findByUserIdOrderByUpdatedDateDesc(userId, pageable);

			List<SearchHistoryResponse> responses = recentSearchesPage.getContent().stream()
					.map(this::mapToSearchHistoryResponse)
					.collect(Collectors.toList());

			return ApiResponse.builder()
					.message(AppConstants.SEARCH_FETCHED_SUCCESSFULLY)
					.data(responses)
					.build();
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse getSearchHistoryByType(String searchType, PaginationRequest paginationRequest) {
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse deleteSearchEntry(String searchId) {
			SearchHistory searchHistory = searchHistoryRepository.findById(searchId)
					.orElseThrow(() -> new BadRequestException(AppConstants.SEARCH_NOT_FOUND));

			searchHistoryRepository.delete(searchHistory);
			return ApiResponse.builder()
					.message(AppConstants.SEARCH_DELETED_SUCCESSFULLY)
					.build();
	}

	@Override
	@Transactional
	public ApiResponse clearAllSearchHistory() {
		String userId = userService.getCurrectUserFromToken().getId();
			searchHistoryRepository.deleteByUserId(userId);
			return ApiResponse.builder()
					.message(AppConstants.SEARCH_DELETED_SUCCESSFULLY)
					.build();
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

}
