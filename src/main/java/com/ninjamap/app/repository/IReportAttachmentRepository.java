package com.ninjamap.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.model.ReportAttachment;

@Repository
public interface IReportAttachmentRepository extends JpaRepository<ReportAttachment, String> {

	/**
	 * Find all attachments for a specific report
	 */
	List<ReportAttachment> findByReportId(String reportId);

	/**
	 * Count attachments for a specific report
	 */
	long countByReportId(String reportId);

	/**
	 * Find attachment by Cloudinary public ID
	 */
	ReportAttachment findByCloudinaryPublicId(String cloudinaryPublicId);

	/**
	 * Delete all attachments for a specific report
	 */
	void deleteByReportId(String reportId);
}
