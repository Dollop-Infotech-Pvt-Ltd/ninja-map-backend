package com.ninjamap.app.service.impl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.kafka.ReportCommentEvent;
import com.ninjamap.app.model.Report;
import com.ninjamap.app.model.ReportComment;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.ReportCommentResponse;
import com.ninjamap.app.payload.response.ReportListItemResponse;
import com.ninjamap.app.repository.IReportCommentRepository;
import com.ninjamap.app.repository.IReportRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IReportService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class ReportServiceImpl implements IReportService {

	@Autowired
	private IReportRepository reportRepository;

	@Autowired
	private IReportCommentRepository reportCommentRepository;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICloudinaryService cloudinaryService;

	private static final long MAX_FILE_SIZE = 5242880;
	private static final String[] ALLOWED_FILE_TYPES = { "image/jpeg", "image/png", "image/gif" ,"image/svg" };

	@Override
	public ApiResponse submitReport(ReportRequest reportRequest) {
		
			reportRepository.save(this.convertRequestToReport(reportRequest));
			return ApiResponse.builder()
					.statusCode(HttpStatus.CREATED.value())
					.message(AppConstants.REPORT_SUBMITTED_SUCCESSFULLY)
					.build();
		
	}

	@Override
	public ApiResponse getReports(PaginationRequest paginationRequest) {
	
			int pageNumber = paginationRequest.getPageNumber() != null ? paginationRequest.getPageNumber() : 0;
			int pageSize = paginationRequest.getPageSize() != null ? paginationRequest.getPageSize() : 10;
			String sortBy = paginationRequest.getSortKey() != null ? paginationRequest.getSortKey() : "createdAt";
			String sortDirection = paginationRequest.getSortDirection() != null ? paginationRequest.getSortDirection()
					: "DESC";

			Sort.Direction direction = Sort.Direction.fromString(sortDirection);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

			String userId = userService.getCurrectUserFromToken().getId();

			Page<Report> reports = reportRepository.findByUserId(userId, pageable);
			
			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message(AppConstants.REPORT_FETCHED_SUCCESSFULLY)
					.data(new PaginatedResponse<>(reports.map(this::convertReportToResponse)))
					.build();

	
	}
	
	private ReportListItemResponse convertReportToResponse(Report report) {
		return ReportListItemResponse.builder()
				.id(report.getId())
				.reportType(report.getReportType())
				.status(report.getStatus())
				.severity(report.getSeverity())
				.comment(report.getComment())
				.latitude(report.getLatitude())
				.longitude(report.getLongitude())
				.address(report.getAddress())
				.userId(report.getUserId())
				.createdDate(report.getCreatedDate())
				.updatedDate(report.getUpdatedDate())
				.reportPicture(report.getReportPicture())
				.build();
	}
	
	private Report convertRequestToReport(ReportRequest reportRequest) {
		String userId = userService.getCurrectUserFromToken().getId();
		String reportPicture = null;
		if (reportRequest.getReportPicture() != null && !reportRequest.getReportPicture().isEmpty()) {
			reportPicture = cloudinaryService.uploadFile(reportRequest.getReportPicture(), AppConstants.CATEGORY_PICTURE);
	     }
		
		return Report.builder()
				.reportType(reportRequest.getReportType())
				.status(ReportStatus.PENDING)
				.severity(ReportSeverity.HIGH)
				.comment(reportRequest.getComment())
				.latitude(reportRequest.getLatitude())
				.longitude(reportRequest.getLongitude())
				.location(reportRequest.getLocation())
				.userId(userId)
				.reportPicture(reportPicture)
				.hideName(reportRequest.getHideName())
				.viewCount(0)
				.helpfulCount(0)
				.notHelpfulCount(0)
				.build();
	}

	@Override
	public ApiResponse getReportById(String reportId) {
			Report report = reportRepository.findById(reportId).orElseThrow(()-> new BadRequestException(AppConstants.REPORT_NOT_FOUND));

			String userId = userService.getCurrectUserFromToken().getId();

			if (!report.getUserId().equals(userId)) {
				throw new BadRequestException("You do not have permission to access this report");
			}

			report.setViewCount(report.getViewCount() + 1);
			reportRepository.save(report);

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message(AppConstants.REPORT_FETCHED_SUCCESSFULLY)
					.data(this.convertReportToResponse(report))
					.build();
	}

	@Override
	public ApiResponse addComment(String reportId, ReportCommentRequest commentRequest) {
		try {
			Report report = reportRepository.findById(reportId).orElse(null);
			if (report == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.NOT_FOUND.value())
						.message("Report not found")
						.data(null)
						.build();
			}

			String userId = userService.getCurrectUserFromToken().getId();

			if (report.getStatus() == ReportStatus.RESOLVED || report.getStatus() == ReportStatus.REJECTED) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Cannot add comments to resolved or rejected reports")
						.data(null)
						.build();
			}

			ReportComment comment = ReportComment.builder()
					.reportId(reportId)
					.userId(userId)
					.comment(commentRequest.getComment())
					.isAdminComment(false)
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();

			ReportComment savedComment = reportCommentRepository.save(comment);

			ReportCommentEvent event = ReportCommentEvent.builder()
					.commentId(savedComment.getId())
					.reportId(savedComment.getReportId())
					.userId(savedComment.getUserId())
					.comment(savedComment.getComment())
					.isAdminComment(savedComment.getIsAdminComment())
					.createdAt(savedComment.getCreatedDate())
					.build();

			try {
//				notificationProducer.sendMessage(kafkaTopics.getNotificationTopic(), event, OutboxType.REPORT_COMMENT);
			} catch (Exception e) {
				System.err.println("Failed to publish report comment event: " + e.getMessage());
			}

			ReportCommentResponse commentResponse = ReportCommentResponse.builder()
					.id(savedComment.getId())
					.reportId(savedComment.getReportId())
					.userId(savedComment.getUserId())
					.comment(savedComment.getComment())
					.isAdminComment(savedComment.getIsAdminComment())
					.createdDate(savedComment.getCreatedDate())
					.updatedDate(savedComment.getUpdatedDate())
					.build();

			return ApiResponse.builder()
					.statusCode(HttpStatus.CREATED.value())
					.message("Comment added successfully")
					.data(commentResponse)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error adding comment: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	@Override
	public ApiResponse getReportTypes() {
		try {
			List<Map<String, Object>> reportTypes = new ArrayList<>();

			for (ReportType type : ReportType.values()) {
				Map<String, Object> typeMap = new HashMap<>();
				typeMap.put("name", type.name());
				typeMap.put("displayName", formatEnumName(type.name()));
				typeMap.put("description", getReportTypeDescription(type));
				reportTypes.add(typeMap);
			}

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message("Report types retrieved successfully")
					.data(reportTypes)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error retrieving report types: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	private String formatEnumName(String enumName) {
		return Arrays.stream(enumName.split("_"))
				.map(word -> word.charAt(0) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	private String getReportTypeDescription(ReportType type) {
		switch (type) {
		case TRAFFIC:
			return "Report traffic congestion or flow issues";
		case ACCIDENT:
			return "Report accidents or collisions";
		case ROAD_CLOSURE:
			return "Report road closures or blockages";
		case EVENT:
			return "Report events or activities";
		case OTHER:
			return "Report other map-related issues";
		default:
			return "Report issue";
		}
	}

}