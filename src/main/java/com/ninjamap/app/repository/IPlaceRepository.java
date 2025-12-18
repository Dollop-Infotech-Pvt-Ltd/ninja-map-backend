package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Place;

@Repository
public interface IPlaceRepository extends JpaRepository<Place, String> {

	/**
	 * Find all places belonging to a user
	 */
	Page<Place> findByUserIdAndIsDeletedFalse(String userId,Pageable pageable);

	/**
	 * Find places by user and category
	 */
	List<Place> findByUserIdAndCategoryIdAndIsDeletedFalse(String userId, String categoryId);

	/**
	 * Find a specific place by ID with deletion check
	 */
	Optional<Place> findByIdAndIsDeletedFalse(String id);

	/**
	 * Find places by category ID
	 */
	List<Place> findByCategoryIdAndIsDeletedFalse(String categoryId);

	/**
	 * Count places by category ID
	 */
	@Query("SELECT COUNT(p) FROM Place p WHERE p.category.id = :categoryId AND p.isDeleted = false")
	Long countByCategoryId(@Param("categoryId") String categoryId);

	/**
	 * Check if a place belongs to a user
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.id = :placeId AND p.userId = :userId AND p.isDeleted = false")
	boolean existsByIdAndUserId(@Param("placeId") String placeId, @Param("userId") String userId);

	/**
	 * Check if user already has a place with the same category
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.userId = :userId AND p.category.id = :categoryId AND p.isDeleted = false")
	boolean existsByUserIdAndCategoryId(@Param("userId") String userId, @Param("categoryId") String categoryId);

	/**
	 * Check if user already has a custom place with the same name
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Place p WHERE p.userId = :userId AND p.name = :name AND p.placeType = 'CUSTOM' AND p.isDeleted = false")
	boolean existsByUserIdAndNameCustom(@Param("userId") String userId, @Param("name") String name);

	/**
	 * Get all category IDs that a user has added category-based places for
	 */
	@Query("SELECT DISTINCT p.category.id FROM Place p WHERE p.userId = :userId AND p.placeType = 'CATEGORY' AND p.isDeleted = false")
	List<String> findUsedCategoryIdsByUserId(@Param("userId") String userId);
}
