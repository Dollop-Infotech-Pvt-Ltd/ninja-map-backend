package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.model.BlogPost;

@Repository
public interface IBlogPostRepository extends JpaRepository<BlogPost, String> {
	@Query("SELECT b FROM BlogPost b WHERE (:category IS NULL OR b.category = :category) And b.isDeleted = false")
	Page<BlogPost> findByCategoryOptional(@Param("category") BlogCategory category, Pageable pageable);

	Optional<BlogPost> findByIdAndIsDeletedFalse(String id);

	// Top 3 latest articles in the same category, excluding current post
	List<BlogPost> findTop3ByCategoryAndIdNotAndIsDeletedFalseOrderByCreatedDateDesc(BlogCategory category,
			String excludedId);

	// Featured posts (optional category)
	@Query("SELECT b FROM BlogPost b " + "WHERE (:category IS NULL OR b.category = :category) "
			+ "AND b.isFeaturedArticle = true AND b.isDeleted = false " + "ORDER BY b.createdDate DESC")
	List<BlogPost> findTopFeaturedByCategory(@Param("category") BlogCategory category, Pageable pageable);

	// Latest posts excluding featured (optional category)
	@Query("SELECT b FROM BlogPost b " + "WHERE (:category IS NULL OR b.category = :category) "
			+ "AND b.isDeleted = false " + "AND (:excludedIds IS NULL OR b.id NOT IN :excludedIds) "
			+ "ORDER BY b.createdDate DESC")
	List<BlogPost> findTopLatestByCategory(@Param("category") BlogCategory category,
			@Param("excludedIds") List<String> excludedIds, Pageable pageable);

}
