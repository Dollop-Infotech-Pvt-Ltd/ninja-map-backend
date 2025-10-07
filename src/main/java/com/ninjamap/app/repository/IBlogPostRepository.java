package com.ninjamap.app.repository;

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
}
