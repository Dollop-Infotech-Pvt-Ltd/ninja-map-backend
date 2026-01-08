package com.ninjamap.app.service;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
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

}
