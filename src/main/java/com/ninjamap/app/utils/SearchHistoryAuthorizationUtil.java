package com.ninjamap.app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ninjamap.app.model.SearchHistory;

public class SearchHistoryAuthorizationUtil {

	/**
	 * Verify if the current user owns the search entry
	 */
	public static boolean isSearchEntryOwner(SearchHistory searchHistory) {
		String currentUserId = getCurrentUserId();
		return searchHistory.getUserId().equals(currentUserId);
	}

	/**
	 * Get current authenticated user ID
	 */
	public static String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

	/**
	 * Check if user is authenticated
	 */
	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated();
	}
}
