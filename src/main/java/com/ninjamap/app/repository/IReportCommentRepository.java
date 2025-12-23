package com.ninjamap.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.ReportComment;

@Repository
public interface IReportCommentRepository extends JpaRepository<ReportComment, String> {

	/**
	 * Find all comments for a specific report
	 */
	List<ReportComment> findByReportIdOrderByCreatedDateAsc(String reportId);

	/**
	 * Find all comments for a specific report with pagination
	 */
	Page<ReportComment> findByReportId(String reportId, Pageable pageable);

	/**
	 * Find all comments by a specific user
	 */
	List<ReportComment> findByUserId(String userId);

	/**
	 * Find all admin comments for a specific report
	 */
	List<ReportComment> findByReportIdAndIsAdminCommentTrue(String reportId);

	/**
	 * Count comments for a specific report
	 */
	long countByReportId(String reportId);

	/**
	 * Count admin comments for a specific report
	 */
	long countByReportIdAndIsAdminCommentTrue(String reportId);
}
