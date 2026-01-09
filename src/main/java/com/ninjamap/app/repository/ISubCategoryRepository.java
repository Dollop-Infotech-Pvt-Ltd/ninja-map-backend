package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.SubCategory;

@Repository
public interface ISubCategoryRepository extends JpaRepository<SubCategory, String> {

	// Standard CRUD methods are inherited from JpaRepository
	
	// Find active subcategories
	Page<SubCategory> findByIsActiveTrue(Pageable pageable);
	
	List<SubCategory> findByIsActiveTrue();
	
	// Find subcategories by category
	List<SubCategory> findByCategoryIdAndIsActiveTrue(String categoryId);
	
	Page<SubCategory> findByCategoryIdAndIsActiveTrue(String categoryId, Pageable pageable);
	
	// Find subcategory by name and category
	Optional<SubCategory> findBySubCategoryNameAndCategoryIdAndIsDeletedFalseAndIsActiveTrue(
			String subCategoryName, String categoryId);
	
	// Find subcategory by name (unique check)
	Optional<SubCategory> findBySubCategoryNameAndIsDeletedFalseAndIsActiveTrue(String subCategoryName);
	
	// Count subcategories by category
	@Query("SELECT COUNT(sc) FROM SubCategory sc WHERE sc.category.id = :categoryId AND sc.isActive = true AND sc.isDeleted = false")
	long countByCategoryIdAndIsActiveTrueAndIsDeletedFalse(@Param("categoryId") String categoryId);
	
	// Find subcategories with search functionality
	@Query("""
			SELECT sc FROM SubCategory sc
			WHERE sc.isDeleted = false
			AND sc.isActive = true
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY sc.createdDate DESC
			""")
	Page<SubCategory> findAllByFilters(@Param("searchValue") String searchValue, Pageable pageable);
	
	// Find subcategories by category with search functionality
	@Query("""
			SELECT sc FROM SubCategory sc
			WHERE sc.category.id = :categoryId
			AND sc.isDeleted = false
			AND sc.isActive = true
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY sc.createdDate DESC
			""")
	Page<SubCategory> findByCategoryIdAndFilters(@Param("categoryId") String categoryId, @Param("searchValue") String searchValue, Pageable pageable);
	
	// Search subcategories across categories (includes category name in search)
	@Query("""
			SELECT sc FROM SubCategory sc
			WHERE sc.isDeleted = false
			AND sc.isActive = true
			AND sc.category.isActive = true
			AND sc.category.isDeleted = false
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			    OR LOWER(sc.category.categoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY sc.createdDate DESC
			""")
	Page<SubCategory> findAllByHierarchicalFilters(@Param("searchValue") String searchValue, Pageable pageable);
	
	// Find subcategories by multiple category IDs
	@Query("""
			SELECT sc FROM SubCategory sc
			WHERE sc.category.id IN :categoryIds
			AND sc.isDeleted = false
			AND sc.isActive = true
			AND (
			    COALESCE(:searchValue, '') = ''
			    OR LOWER(sc.subCategoryName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			)
			ORDER BY sc.createdDate DESC
			""")
	Page<SubCategory> findByCategoryIdsAndFilters(@Param("categoryIds") List<String> categoryIds, @Param("searchValue") String searchValue, Pageable pageable);
	
	// Check if subcategory name exists
	@Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM SubCategory sc WHERE sc.subCategoryName = :subCategoryName AND sc.isDeleted = false")
	boolean existsBySubCategoryNameAndIsDeletedFalse(@Param("subCategoryName") String subCategoryName);
}