package com.ninjamap.app.utils;

import com.ninjamap.app.model.SearchHistory.SearchType;

public class SearchHistoryValidator {

	private static final int MIN_SEARCH_TERM_LENGTH = 1;
	private static final int MAX_SEARCH_TERM_LENGTH = 255;

	/**
	 * Validate search term
	 */
	public static boolean isValidSearchTerm(String searchTerm) {
		if (searchTerm == null) {
			return false;
		}

		String trimmed = searchTerm.trim();
		return trimmed.length() >= MIN_SEARCH_TERM_LENGTH && trimmed.length() <= MAX_SEARCH_TERM_LENGTH;
	}

	/**
	 * Validate search type
	 */
	public static boolean isValidSearchType(String searchType) {
		if (searchType == null) {
			return false;
		}

		try {
			SearchType.valueOf(searchType.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Get error message for invalid search term
	 */
	public static String getSearchTermErrorMessage() {
		return String.format("Search term must be between %d and %d characters", MIN_SEARCH_TERM_LENGTH, MAX_SEARCH_TERM_LENGTH);
	}

	/**
	 * Get error message for invalid search type
	 */
	public static String getSearchTypeErrorMessage() {
		return "Invalid search type. Valid types are: PLACE_SEARCH, CATEGORY_SEARCH, LOCATION_SEARCH";
	}
}
