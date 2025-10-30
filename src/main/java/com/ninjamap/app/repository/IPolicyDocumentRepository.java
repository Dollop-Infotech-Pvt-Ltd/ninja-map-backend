package com.ninjamap.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ninjamap.app.enums.DocumentType;
import com.ninjamap.app.model.PolicyDocument;
import com.ninjamap.app.utils.constants.AppConstants;

public interface IPolicyDocumentRepository extends JpaRepository<PolicyDocument, String> {

	@Query("SELECT p FROM PolicyDocument p WHERE p.id = :id AND p.isDeleted = false AND (:isActive IS NULL OR p.isActive = :isActive)")
	Optional<PolicyDocument> findByIdAndOptionalIsActive(@Param(AppConstants.ID) String id,
			@Param(AppConstants.IS_ACTIVE) Boolean isActive);

	@Query("SELECT p FROM PolicyDocument p WHERE (:isActive IS NULL OR p.isActive = :isActive) AND p.isDeleted = false AND (:documentType IS NULL OR p.type = :documentType)")
	Page<PolicyDocument> findAllByFilters(@Param(AppConstants.IS_ACTIVE) Boolean isActive,
			@Param(AppConstants.DOCUMENT_TYPE) DocumentType documentType, Pageable pageable);

	Optional<PolicyDocument> findByTypeAndIsDeletedFalse(DocumentType documentType);

	boolean existsByTypeAndTitleAndIsDeletedFalse(DocumentType type, String title);

	@Query("SELECT p FROM PolicyDocument p WHERE (:isActive IS NULL OR p.isActive = :isActive) AND p.isDeleted = false AND (:documentType IS NULL OR p.type = :documentType)")
	List<PolicyDocument> findAllByFiltersWithoutPagination(@Param("isActive") Boolean isActive,
			@Param("documentType") DocumentType documentType);

	@Query("""
			SELECT p
			FROM PolicyDocument p
			WHERE p.isDeleted = false
			AND (:documentType IS NULL OR p.type = :documentType)
			AND (:searchValue IS NULL OR :searchValue = ''
			     OR LOWER(p.title) LIKE LOWER(CONCAT('%', :searchValue, '%'))
			     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
			""")
	Page<PolicyDocument> searchByTypeAndSearchValue(@Param("documentType") DocumentType documentType,
			@Param("searchValue") String searchValue, Pageable pageable);

	boolean existsByTypeAndTitleAndIsDeletedFalseAndIdNot(DocumentType type, String title, String id);

}
