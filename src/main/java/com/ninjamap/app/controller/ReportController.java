package com.ninjamap.app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
import com.ninjamap.app.payload.request.StatusUpdateRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IReportService;
import com.ninjamap.app.service.IUserService;
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

	@Autowired
	private IUserService userService;

	/**
	 * Submit a new report
	 */
	@PostMapping("/submit")
	public ResponseEntity<ApiResponse> submitReport(@Valid ReportRequest reportRequest) {
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
	 * Get nearby reports by geographic location within 5km radius with pagination
	 */
	@GetMapping("/nearby")
	public ResponseEntity<ApiResponse> getNearbyReports(
			@RequestParam(name = "latitude") Double latitude,
			@RequestParam(name = "longitude") Double longitude,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "severity", required = false) String severity) {

		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageSize(pageSize)
				.pageNumber(pageNumber)
				.build();

		return new ResponseEntity<>(
				reportService.getReportsByLocation(latitude, longitude, paginationRequest, status, severity),
				HttpStatus.OK);
	}

	/**
	 * Update a report's status with authorization checks
	 */
	@PostMapping("/status")
	public ResponseEntity<ApiResponse> updateReportStatus(
			@Valid @RequestBody StatusUpdateRequest statusUpdateRequest) {
		
		// Get current user for authorization check
		String userId = userService.getCurrectUserFromToken().getId();
		
		// Call service to update status
		ApiResponse response = reportService.updateReportStatus(statusUpdateRequest , userId);
		
		// Return appropriate HTTP status based on response
		HttpStatus status = HttpStatus.valueOf(response.getStatusCode());
		return new ResponseEntity<>(response, status);
	}

	/**
	 * Get reports filtered by status with pagination
	 */
	@GetMapping("/filter/status")
	public ResponseEntity<ApiResponse> getReportsByStatus(
			@RequestParam(name = "status") String statusParam,
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey) {
		
		try {
			// Parse status parameter
			ReportStatus status = ReportStatus.valueOf(statusParam.toUpperCase());
			
			PaginationRequest paginationRequest = PaginationRequest.builder()
					.pageSize(pageSize)
					.pageNumber(pageNumber)
					.sortDirection(sortDirection)
					.sortKey(sortKey)
					.build();
			
			return new ResponseEntity<>(reportService.getReportsByStatus(status, paginationRequest), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Invalid status value: " + statusParam)
						.build(),
					HttpStatus.BAD_REQUEST);
		}
	}

	
}
