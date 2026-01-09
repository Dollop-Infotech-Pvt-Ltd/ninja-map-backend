package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, String> {

	Page<Category> findByIsActiveTrue(Pageable pageable);
	
	List<Category> findByIsActiveTrue();

	Optional<Category> findByCategoryNameAndIsDeletedFalseAndIsActiveTrue(String categoryName);
	
	// Find categories with their subcategories (eager loading)
	@Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories sc WHERE c.isActive = true AND c.isDeleted = false AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false))")
	List<Category> findCategoriesWithSubCategories();
	
	// Find categories with their subcategories (paginated)
	@Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories sc WHERE c.isActive = true AND c.isDeleted = false AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false))")
	Page<Category> findCategoriesWithSubCategories(Pageable pageable);
	
	// Find category with subcategories by category ID
	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories sc WHERE c.id = :categoryId AND c.isActive = true AND c.isDeleted = false AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false))")
	Optional<Category> findCategoryWithSubCategoriesById(@Param("categoryId") String categoryId);
	
	// Find categories that have at least one subcategory
	@Query("SELECT DISTINCT c FROM Category c INNER JOIN c.subCategories sc WHERE c.isActive = true AND c.isDeleted = false AND sc.isActive = true AND sc.isDeleted = false")
	List<Category> findCategoriesWithActiveSubCategories();
	
	// Count subcategories for each category
	@Query("SELECT c.id, COUNT(sc) FROM Category c LEFT JOIN c.subCategories sc WHERE c.isActive = true AND c.isDeleted = false AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false)) GROUP BY c.id")
	List<Object[]> countSubCategoriesPerCategory();
	
	// Enhanced search functionality
	@Query("""
			SELECT DISTINCT c FROM Category c
			WHERE c.isDeleted = false
			AND c.isActive = true
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY c.createdDate DESC
			""")
	Page<Category> findAllByFilters(@Param("searchValue") String searchValue, Pageable pageable);
	
	// Search categories with subcategories (hierarchical search)
	@Query("""
			SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories sc
			WHERE c.isDeleted = false
			AND c.isActive = true
			AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false))
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY c.createdDate DESC
			""")
	Page<Category> findCategoriesWithSubCategoriesByFilters(@Param("searchValue") String searchValue, Pageable pageable);
	
	// Find categories that contain subcategories matching search term
	@Query("""
			SELECT DISTINCT c FROM Category c INNER JOIN c.subCategories sc
			WHERE c.isDeleted = false
			AND c.isActive = true
			AND sc.isActive = true
			AND sc.isDeleted = false
			AND LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			ORDER BY c.createdDate DESC
			""")
	Page<Category> findCategoriesBySubCategoryName(@Param("searchValue") String searchValue, Pageable pageable);
	
	// Find categories with subcategories, filtered by category name only
	@Query("""
			SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories sc
			WHERE c.isDeleted = false
			AND c.isActive = true
			AND (sc IS NULL OR (sc.isActive = true AND sc.isDeleted = false))
			AND LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			ORDER BY c.createdDate DESC
			""")
	Page<Category> findCategoriesWithSubCategoriesByCategoryName(@Param("searchValue") String searchValue, Pageable pageable);
}
