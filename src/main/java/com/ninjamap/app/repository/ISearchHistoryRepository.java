package com.ninjamap.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ninjamap.app.model.SearchHistory;
import com.ninjamap.app.model.SearchHistory.SearchType;

@Repository
public interface ISearchHistoryRepository extends JpaRepository<SearchHistory, String> {

	Page<SearchHistory> findByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);

	Page<SearchHistory> findByUserIdAndSearchTypeOrderByCreatedDateDesc(String userId, SearchType searchType, Pageable pageable);

	@Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId AND sh.searchTerm = :searchTerm AND sh.createdDate > :fiveMinutesAgo ORDER BY sh.createdDate DESC")
	Optional<SearchHistory> findRecentDuplicateSearch(@Param("userId") String userId, @Param("searchTerm") String searchTerm, @Param("fiveMinutesAgo") LocalDateTime fiveMinutesAgo);

	@Modifying
	@Query("DELETE FROM SearchHistory sh WHERE sh.createdDate < :cutoffDate")
	void deleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

	void deleteByUserId(String userId);

	Long countByUserId(String userId);

	@Query("SELECT sh FROM SearchHistory sh WHERE sh.userId = :userId ORDER BY sh.createdDate ASC")
	List<SearchHistory> findOldestEntriesForUser(@Param("userId") String userId, Pageable pageable);
}
