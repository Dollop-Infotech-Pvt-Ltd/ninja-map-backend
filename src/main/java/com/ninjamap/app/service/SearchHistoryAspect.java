package com.ninjamap.app.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class SearchHistoryAspect {

	@Autowired(required = false)
	private ISearchHistoryService searchHistoryService;

	/**
	 * Intercept place search operations and record search history
	 */
	@AfterReturning("execution(* com.ninjamap.app.service.IPlaceService.getPlacesByUserId(..))")
	public void recordPlaceSearch(JoinPoint joinPoint) {
		try {
			if (searchHistoryService != null) {
				// Extract search term from pagination request if available
				Object[] args = joinPoint.getArgs();
				if (args.length > 0) {
					String searchTerm = extractSearchTerm(args[0]);
					if (searchTerm != null && !searchTerm.isEmpty()) {
						SearchHistoryRequest request = SearchHistoryRequest.builder()
								.searchTerm(searchTerm)
								.build();
						searchHistoryService.recordSearch(request);
					}
				}
			}
		} catch (Exception e) {
			log.warn("Failed to record place search history", e);
		}
	}

	/**
	 * Intercept category search operations and record search history
	 */
	@AfterReturning("execution(* com.ninjamap.app.service.IPlaceService.getPlacesByCategory(..))")
	public void recordCategorySearch(JoinPoint joinPoint) {
		try {
			if (searchHistoryService != null) {
				Object[] args = joinPoint.getArgs();
				if (args.length > 0 && args[0] instanceof String) {
					String categoryId = (String) args[0];
					SearchHistoryRequest request = SearchHistoryRequest.builder()
							.searchTerm(categoryId)
							.build();
					searchHistoryService.recordSearch(request);
				}
			}
		} catch (Exception e) {
			log.warn("Failed to record category search history", e);
		}
	}

	/**
	 * Helper method to extract search term from request object
	 */
	private String extractSearchTerm(Object requestObject) {
		try {
			if (requestObject != null && requestObject.getClass().getName().contains("PaginationRequest")) {
				// Try to get searchValue from PaginationRequest
				Object searchValue = requestObject.getClass().getMethod("getSearchValue").invoke(requestObject);
				if (searchValue instanceof String) {
					return (String) searchValue;
				}
			}
		} catch (Exception e) {
			log.debug("Could not extract search term from request", e);
		}
		return null;
	}
}
