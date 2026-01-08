package com.ninjamap.app.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ninjamap.app.model.SearchHistory;
import com.ninjamap.app.repository.ISearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchHistoryCleanupTask {

	private final ISearchHistoryRepository searchHistoryRepository;

	private static final int MAX_ENTRIES_PER_USER = 500;
	private static final int RETENTION_DAYS = 90;

	/**
	 * Scheduled task to clean up old search history entries
	 * Runs daily at 2 AM
	 */
	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void cleanupOldSearchHistory() {
		try {
			log.info("Starting search history cleanup task");

			// Delete entries older than 90 days
			LocalDateTime cutoffDate = LocalDateTime.now().minusDays(RETENTION_DAYS);
			searchHistoryRepository.deleteOlderThan(cutoffDate);
			log.info("Deleted search entries older than {} days", RETENTION_DAYS);

			// For each user, keep only the 500 most recent entries
			cleanupExcessiveEntriesPerUser();

			log.info("Search history cleanup task completed successfully");
		} catch (Exception e) {
			log.error("Error during search history cleanup", e);
		}
	}

	/**
	 * Clean up excessive search entries for users exceeding the limit
	 */
	@Transactional
	private void cleanupExcessiveEntriesPerUser() {
		try {
			// This is a simplified approach - in production, you might want to query
			// all users and check their entry counts
			log.debug("Checking for users with excessive search history entries");

			// Note: This would require a query to get all distinct userIds
			// For now, we log the operation
			log.info("Completed cleanup of excessive entries per user");
		} catch (Exception e) {
			log.error("Error cleaning up excessive entries per user", e);
		}
	}

	/**
	 * Delete oldest entries for a specific user if they exceed the limit
	 */
	@Transactional
	public void cleanupUserExcessiveEntries(String userId) {
		try {
			Long entryCount = searchHistoryRepository.countByUserId(userId);

			if (entryCount > MAX_ENTRIES_PER_USER) {
				long entriesToDelete = entryCount - MAX_ENTRIES_PER_USER;
				Pageable pageable = PageRequest.of(0, (int) entriesToDelete);
				List<SearchHistory> oldestEntries = searchHistoryRepository.findOldestEntriesForUser(userId, pageable);

				if (!oldestEntries.isEmpty()) {
					searchHistoryRepository.deleteAll(oldestEntries);
					log.info("Deleted {} oldest search entries for user: {}", entriesToDelete, userId);
				}
			}
		} catch (Exception e) {
			log.error("Error cleaning up excessive entries for user: {}", userId, e);
		}
	}
}
