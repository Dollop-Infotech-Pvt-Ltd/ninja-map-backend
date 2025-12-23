package com.ninjamap.app.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import com.ninjamap.app.model.Report;

@Repository
public interface IReportRepository extends JpaRepository<Report, String> {

	/**
	 * Find reports by user ID with pagination
	 */
	Page<Report> findByUserId(String userId, Pageable pageable);

	/**
	 * Find reports by status with pagination
	 */
	Page<Report> findByStatus(ReportStatus status, Pageable pageable);

	/**
	 * Find reports by type with pagination
	 */
	Page<Report> findByReportType(ReportType reportType, Pageable pageable);

	/**
	 * Find reports by user ID and status with pagination
	 */
	Page<Report> findByUserIdAndStatus(String userId, ReportStatus status, Pageable pageable);

	/**
	 * Find reports by user ID and type with pagination
	 */
	Page<Report> findByUserIdAndReportType(String userId, ReportType reportType, Pageable pageable);

	/**
	 * Find reports by status and type with pagination
	 */
	Page<Report> findByStatusAndReportType(ReportStatus status, ReportType reportType, Pageable pageable);

	/**
	 * Find reports by date range with pagination
	 */
	Page<Report> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	/**
	 * Find reports by user ID within a date range with pagination
	 */
	Page<Report> findByUserIdAndCreatedDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	/**
	 * Geospatial query to find reports within a radius of a location
	 * Uses PostGIS ST_DWithin function to find points within distance
	 */
//	@Query(value = "SELECT r FROM Report r WHERE ST_DWithin(r.location, ST_MakePoint(:longitude, :latitude), :radiusInMeters) = true")
//	Page<Report> findReportsWithinRadius(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("radiusInMeters") Double radiusInMeters, Pageable pageable);

	/**
	 * Find reports by status excluding rejected and archived
	 */
	@Query("SELECT r FROM Report r WHERE r.status NOT IN ('REJECTED', 'ARCHIVED')")
	Page<Report> findActiveReports(Pageable pageable);

	/**
	 * Find reports by title or description containing search term
	 */
	@Query("SELECT r FROM Report r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<Report> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

	/**
	 * Find reports by address containing search term
	 */
	@Query("SELECT r FROM Report r WHERE LOWER(r.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<Report> searchByAddress(@Param("searchTerm") String searchTerm, Pageable pageable);

	/**
	 * Count reports by status
	 */
	long countByStatus(ReportStatus status);

	/**
	 * Count reports by type
	 */
	long countByReportType(ReportType reportType);

	/**
	 * Count reports by user ID
	 */
	long countByUserId(String userId);
}
