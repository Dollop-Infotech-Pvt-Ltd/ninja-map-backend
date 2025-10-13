package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ninjamap.app.model.FAQ;

public interface IFAQRepository extends JpaRepository<FAQ, String> {
	// Fetch only FAQs that are not deleted
	List<FAQ> findAllByIsDeletedFalse();

	Optional<FAQ> findByIdAndIsDeletedFalse(String id);

	// Check if a category already exists (not deleted)
	boolean existsByCategoryAndIsDeletedFalse(String category);

	// Fetch all questions under a category (for duplicate question check)
	@Query("SELECT qa.question FROM FAQ f JOIN f.questions qa WHERE f.category = :category AND f.isDeleted = false")
	List<String> findQuestionsByCategory(@Param("category") String category);

	@Query("""
			SELECT f
			FROM FAQ f
			LEFT JOIN f.questions q
			WHERE f.isDeleted = false
			AND (:searchValue IS NULL
			     OR :searchValue = ''
			     OR LOWER(f.category) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			     OR LOWER(q.question) LIKE LOWER(CONCAT('%', :searchValue, '%')))
			""")
	Page<FAQ> searchByCategoryOrQuestions(@Param("searchValue") String searchValue, Pageable pageable);

}
