package com.ninjamap.app.service;

import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
import com.ninjamap.app.payload.request.StatusUpdateRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IReportService {

	/**
	 * Submit a new report
	 */
	ApiResponse submitReport(ReportRequest reportRequest);

	/**
	 * Get paginated reports with optional filters
	 */
	ApiResponse getReports(PaginationRequest paginationRequest);

	/**
	 * Get a specific report by ID
	 */
	ApiResponse getReportById(String reportId);

	/**
	 * Add a comment to a report
	 */
	ApiResponse addComment(String reportId, ReportCommentRequest commentRequest);

	/**
	 * Get all available report types
	 */
	ApiResponse getReportTypes();

	/**
	 * Get reports within 5km radius from geographic location with pagination
	 * 
	 * @param latitude Center point latitude (-90 to 90)
	 * @param longitude Center point longitude (-180 to 180)
	 * @param paginationRequest Pagination parameters (pageSize, pageNumber, sortDirection, sortKey)
	 * @param status Optional filter by report status
	 * @param severity Optional filter by report severity
	 * @return ApiResponse containing paginated reports within 5km radius sorted by distance and creation date
	 */
	ApiResponse getReportsByLocation(Double latitude, Double longitude, 
			PaginationRequest paginationRequest, String status, String severity);

	/**
	 * Update a report's status with validation and audit trail
	 * 
	 * @param reportId The ID of the report to update
	 * @param newStatus The new status to transition to
	 * @param userId The ID of the user making the update
	 * @return ApiResponse containing the updated report or error details
	 */
	ApiResponse updateReportStatus( StatusUpdateRequest newStatus, String userId);

	/**
	 * Get reports filtered by status with pagination
	 * 
	 * @param status The report status to filter by
	 * @param paginationRequest Pagination parameters (pageSize, pageNumber, sortDirection, sortKey)
	 * @return ApiResponse containing paginated reports matching the status filter
	 */
	ApiResponse getReportsByStatus(ReportStatus status, PaginationRequest paginationRequest);

}
