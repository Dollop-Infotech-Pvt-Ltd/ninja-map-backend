package com.ninjamap.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import com.ninjamap.app.model.Report;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.StatusUpdateRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.IReportCommentRepository;
import com.ninjamap.app.repository.IReportRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.StatusTransitionValidator;

@ExtendWith(MockitoExtension.class)
class ReportServiceStatusManagementTest {

	@Mock
	private IReportRepository reportRepository;

	@Mock
	private IReportCommentRepository reportCommentRepository;

	@Mock
	private IUserService userService;

	@Mock
	private ICloudinaryService cloudinaryService;

	@Mock
	private StatusTransitionValidator statusTransitionValidator;

	@InjectMocks
	private ReportServiceImpl reportService;

	private Report testReport;
	private String reportId;
	private String userId;
	private StatusUpdateRequest statusUpdateRequest;

	@BeforeEach
	void setUp() {
		reportId = "test-report-id";
		userId = "test-user-id";

		testReport = Report.builder()
				.id(reportId)
				.reportType(ReportType.TRAFFIC)
				.status(ReportStatus.PENDING)
				.severity(ReportSeverity.HIGH)
				.comment("Test report")
				.latitude(40.7128)
				.longitude(-74.0060)
				.address("Test Address")
				.userId(userId)
				.viewCount(0)
				.helpfulCount(0)
				.notHelpfulCount(0)
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
	}

	@Test
	void testUpdateReportStatus_ValidTransition_Success() {
		ReportStatus newStatus = ReportStatus.UNDER_REVIEW;
		when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));
		when(statusTransitionValidator.isValidTransition(ReportStatus.PENDING, newStatus)).thenReturn(true);
		when(reportRepository.save(any(Report.class))).thenReturn(testReport);

		ApiResponse response = reportService.updateReportStatus(statusUpdateRequest, userId);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("Report status updated successfully", response.getMessage());
		assertNotNull(response.getData());
		verify(reportRepository, times(1)).findById(reportId);
		verify(reportRepository, times(1)).save(any(Report.class));
	}

	@Test
	void testUpdateReportStatus_InvalidTransition_BadRequest() {
		ReportStatus newStatus = ReportStatus.ARCHIVED;
		when(reportRepository.findById(reportId)).thenReturn(Optional.of(testReport));
		when(statusTransitionValidator.isValidTransition(ReportStatus.PENDING, newStatus)).thenReturn(false);
		when(statusTransitionValidator.getTransitionErrorMessage(ReportStatus.PENDING, newStatus))
				.thenReturn("Cannot transition from PENDING to ARCHIVED");

		ApiResponse response = reportService.updateReportStatus(statusUpdateRequest, userId);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
		assertEquals("Cannot transition from PENDING to ARCHIVED", response.getMessage());
		assertNull(response.getData());
		verify(reportRepository, times(1)).findById(reportId);
		verify(reportRepository, never()).save(any(Report.class));
	}

	@Test
	void testUpdateReportStatus_ReportNotFound_NotFound() {
		ReportStatus newStatus = ReportStatus.UNDER_REVIEW;
		when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

		ApiResponse response = reportService.updateReportStatus(statusUpdateRequest, userId);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
		assertEquals("Report not found", response.getMessage());
		assertNull(response.getData());
		verify(reportRepository, times(1)).findById(reportId);
		verify(reportRepository, never()).save(any(Report.class));
	}

	@Test
	void testGetReportsByStatus_Success() {
		ReportStatus status = ReportStatus.PENDING;
		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageNumber(0)
				.pageSize(10)
				.sortDirection("DESC")
				.build();

		Page<Report> reportPage = new PageImpl<>(java.util.List.of(testReport), PageRequest.of(0, 10), 1);
		when(reportRepository.findByStatus(status, PageRequest.of(0, 10))).thenReturn(reportPage);

		ApiResponse response = reportService.getReportsByStatus(status, paginationRequest);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("Reports retrieved successfully", response.getMessage());
		assertNotNull(response.getData());
		assertTrue(response.getData() instanceof PaginatedResponse);
		verify(reportRepository, times(1)).findByStatus(status, PageRequest.of(0, 10));
	}

	@Test
	void testGetReportsByStatus_NullStatus_BadRequest() {
		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageNumber(0)
				.pageSize(10)
				.sortDirection("DESC")
				.build();

		ApiResponse response = reportService.getReportsByStatus(null, paginationRequest);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
		assertEquals("Status filter is required", response.getMessage());
		assertNull(response.getData());
		verify(reportRepository, never()).findByStatus(any(), any());
	}

	@Test
	void testGetReportsByStatus_EmptyResult() {
		ReportStatus status = ReportStatus.RESOLVED;
		PaginationRequest paginationRequest = PaginationRequest.builder()
				.pageNumber(0)
				.pageSize(10)
				.sortDirection("DESC")
				.build();

		Page<Report> emptyPage = new PageImpl<>(java.util.List.of(), PageRequest.of(0, 10), 0);
		when(reportRepository.findByStatus(status, PageRequest.of(0, 10))).thenReturn(emptyPage);

		ApiResponse response = reportService.getReportsByStatus(status, paginationRequest);

		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertEquals("Reports retrieved successfully", response.getMessage());
		assertNotNull(response.getData());
		verify(reportRepository, times(1)).findByStatus(status, PageRequest.of(0, 10));
	}
}
