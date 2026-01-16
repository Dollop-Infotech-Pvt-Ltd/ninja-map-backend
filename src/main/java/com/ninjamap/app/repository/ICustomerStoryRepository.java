package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.StoryCategory;
import com.ninjamap.app.model.CustomerStory;

@Repository
public interface ICustomerStoryRepository extends JpaRepository<CustomerStory, String> {

	Optional<CustomerStory> findByIdAndIsDeletedFalse(String id);

	Page<CustomerStory> findByIsDeletedFalseAndIsActiveTrueAndIsApprovedTrueOrderByCreatedDateDesc(Pageable pageable);

	Page<CustomerStory> findByIsDeletedFalseAndIsActiveTrueAndCategoryOrderByCreatedDateDesc(StoryCategory category,Pageable pageable);

	Page<CustomerStory> findByIsDeletedFalseAndIsActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedDateDesc(
			String title, Pageable pageable);

	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isActive = true " +
			"AND (LOWER(cs.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
			"OR LOWER(cs.description) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
			"OR LOWER(cs.title) LIKE LOWER(CONCAT('%', :searchValue, '%'))) " +
			"ORDER BY cs.createdDate DESC")
	Page<CustomerStory> searchStories(@Param("searchValue") String searchValue, Pageable pageable);

	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isActive = true " +
			"AND cs.category = :category " +
			"AND (LOWER(cs.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
			"OR LOWER(cs.description) LIKE LOWER(CONCAT('%', :searchValue, '%')) " +
			"OR LOWER(cs.authorName) LIKE LOWER(CONCAT('%', :searchValue, '%'))) " +
			"ORDER BY cs.createdDate DESC")
	Page<CustomerStory> searchStoriesByCategory(@Param("category") StoryCategory category,
			@Param("searchValue") String searchValue, Pageable pageable);

	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isActive = true " +
			"ORDER BY cs.stats.likes DESC, cs.createdDate DESC")
	Page<CustomerStory> findTopStoriesByLikes(Pageable pageable);

	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isActive = true " +
			"AND cs.category = :category ORDER BY cs.stats.likes DESC, cs.createdDate DESC")
	Page<CustomerStory> findTopStoriesByCategoryAndLikes(@Param("category") StoryCategory category, Pageable pageable);

	long countByIsDeletedFalseAndIsActiveTrue();

	long countByIsDeletedFalseAndIsActiveTrueAndCategory(StoryCategory category);
	
	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isApproved = true ORDER BY cs.createdDate DESC")
	Page<CustomerStory> findApprovedStories(Pageable pageable);

	@Query("SELECT cs FROM CustomerStory cs WHERE cs.isDeleted = false AND cs.isApproved = false ORDER BY cs.createdDate DESC")
	Page<CustomerStory> findPendingStories(Pageable pageable);
}
