package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IReportService;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

	@Autowired
	private IReportService reportService;

	/**
	 * Submit a new report
	 */
	@PostMapping("/submit")
	public ResponseEntity<ApiResponse> submitReport(@Valid @RequestBody ReportRequest reportRequest) {
		return new ResponseEntity<>(reportService.submitReport(reportRequest), HttpStatus.CREATED);
	}

	/**
	 * Get paginated reports with optional filters
	 */
	@GetMapping
	public ResponseEntity<ApiResponse> getReports(
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.sortDirection(sortDirection)
				.sortKey(sortKey)
				.build();

		return new ResponseEntity<>(reportService.getReports(paginationRequest), HttpStatus.OK);
	}

	/**
	 * Get a specific report by ID
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> getReportById(@PathVariable String id) {
		return new ResponseEntity<>(reportService.getReportById(id), HttpStatus.OK);
	}

	/**
	 * Search reports by query string
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse> searchReports(
			@RequestParam(name = "query") String query,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.build();

		return new ResponseEntity<>(reportService.searchReports(query, paginationRequest), HttpStatus.OK);
	}

	/**
	 * Add a comment to a report
	 */
	@PostMapping("/{id}/comments")
	public ResponseEntity<ApiResponse> addComment(
			@PathVariable String id,
			@Valid @RequestBody ReportCommentRequest commentRequest) {
		return new ResponseEntity<>(reportService.addComment(id, commentRequest), HttpStatus.CREATED);
	}

	/**
	 * Get all available report types
	 */
	@GetMapping("/types")
	public ResponseEntity<ApiResponse> getReportTypes() {
		return new ResponseEntity<>(reportService.getReportTypes(), HttpStatus.OK);
	}

	/**
	 * Upload attachments to a report
	 */
	@PostMapping("/{id}/attachments")
	public ResponseEntity<ApiResponse> uploadAttachments(
			@PathVariable String id,
			@RequestParam("files") MultipartFile[] files) {
		return new ResponseEntity<>(reportService.uploadAttachments(id, files), HttpStatus.CREATED);
	}

	/**
	 * Delete an attachment from a report
	 */
	@DeleteMapping("/{reportId}/attachments/{attachmentId}")
	public ResponseEntity<ApiResponse> deleteAttachment(
			@PathVariable String reportId,
			@PathVariable String attachmentId) {
		return new ResponseEntity<>(reportService.deleteAttachment(reportId, attachmentId), HttpStatus.OK);
	}

	/**
	 * Search reports within a geographic radius
	 */
	@GetMapping("/search/location")
	public ResponseEntity<ApiResponse> searchReportsWithinRadius(
			@RequestParam(name = "latitude") Double latitude,
			@RequestParam(name = "longitude") Double longitude,
			@RequestParam(name = "radius", required = false) Double radiusInKm,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.build();

		return new ResponseEntity<>(reportService.searchReportsWithinRadius(latitude, longitude, radiusInKm, paginationRequest), HttpStatus.OK);
	}
}
