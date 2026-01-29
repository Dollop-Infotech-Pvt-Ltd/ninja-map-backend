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
	 * Find reports by comment containing search term
	 */
	@Query("SELECT r FROM Report r WHERE  LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<Report> searchByComment(@Param("searchTerm") String searchTerm, Pageable pageable);

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

	/**
	 * Find nearby reports using Haversine formula for distance calculation
	 * Calculates distance between provided coordinates and report coordinates
	 * Results are sorted by distance ascending, then by creation date descending
	 * 
	 * @param latitude Center point latitude (-90 to 90)
	 * @param longitude Center point longitude (-180 to 180)
	 * @param limit Maximum number of results to retrieve
	 * @param pageable Pagination information
	 * @return Page of nearby reports sorted by distance and creation date
	 */
	@Query(value = "SELECT r.* FROM reports r " +
			"ORDER BY " +
			"(6371 * acos(cos(radians(90 - :latitude)) * cos(radians(90 - r.latitude)) + " +
			"sin(radians(90 - :latitude)) * sin(radians(90 - r.latitude)) * " +
			"cos(radians(:longitude - r.longitude)))) ASC, " +
			"r.created_date DESC " +
			"LIMIT :limit",
			nativeQuery = true,
			countQuery = "SELECT COUNT(*) FROM reports")
	Page<Report> findNearbyReports(
			@Param("latitude") Double latitude,
			@Param("longitude") Double longitude,
			@Param("limit") Integer limit,
			Pageable pageable);

	@Query(
		    value = """
		        SELECT r.* 
		        FROM reports r
		        WHERE (
		            6371 * acos(
		                cos(radians(:latitude)) * cos(radians(r.latitude)) *
		                cos(radians(r.longitude) - radians(:longitude)) +
		                sin(radians(:latitude)) * sin(radians(r.latitude))
		            )
		        ) <= :radiusKm
		        ORDER BY
		            (
		                6371 * acos(
		                    cos(radians(:latitude)) * cos(radians(r.latitude)) *
		                    cos(radians(r.longitude) - radians(:longitude)) +
		                    sin(radians(:latitude)) * sin(radians(r.latitude))
		                )
		            ) ASC,
		            r.created_date DESC
		        """,
		    countQuery = """
		        SELECT COUNT(*)
		        FROM reports r
		        WHERE (
		            6371 * acos(
		                cos(radians(:latitude)) * cos(radians(r.latitude)) *
		                cos(radians(r.longitude) - radians(:longitude)) +
		                sin(radians(:latitude)) * sin(radians(r.latitude))
		            )
		        ) <= :radiusKm
		        """,
		    nativeQuery = true
		)
		Page<Report> findReportsWithinRadius(
		        @Param("latitude") Double latitude,
		        @Param("longitude") Double longitude,
		        @Param("radiusKm") Double radiusKm,
		        Pageable pageable
		);

}
