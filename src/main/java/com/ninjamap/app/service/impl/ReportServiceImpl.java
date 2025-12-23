package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.OutboxType;
import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import com.ninjamap.app.kafka.KafkaTopics;
import com.ninjamap.app.kafka.NotificationProducer;
import com.ninjamap.app.kafka.ReportCommentEvent;
import com.ninjamap.app.kafka.ReportSubmissionEvent;
import com.ninjamap.app.model.Report;
import com.ninjamap.app.model.ReportAttachment;
import com.ninjamap.app.model.ReportComment;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.ReportCommentRequest;
import com.ninjamap.app.payload.request.ReportRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.ReportAttachmentResponse;
import com.ninjamap.app.payload.response.ReportCommentResponse;
import com.ninjamap.app.payload.response.ReportListItemResponse;
import com.ninjamap.app.payload.response.ReportResponse;
import com.ninjamap.app.repository.IReportAttachmentRepository;
import com.ninjamap.app.repository.IReportCommentRepository;
import com.ninjamap.app.repository.IReportRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IReportService;
import com.ninjamap.app.service.IUserService;

@Service
public class ReportServiceImpl implements IReportService {

	@Autowired
	private IReportRepository reportRepository;

	@Autowired
	private IReportCommentRepository reportCommentRepository;

	@Autowired
	private IReportAttachmentRepository reportAttachmentRepository;

	@Autowired
	private IUserService userService;

	@Autowired
	private ICloudinaryService cloudinaryService;

	@Autowired
	private NotificationProducer notificationProducer;

	@Autowired
	private KafkaTopics kafkaTopics;

	private static final GeometryFactory geometryFactory = new GeometryFactory();
	private static final long MAX_FILE_SIZE = 5242880;
	private static final int MAX_ATTACHMENTS = 5;
	private static final String[] ALLOWED_FILE_TYPES = { "image/jpeg", "image/png", "image/gif" };

	@Override
	public ApiResponse submitReport(ReportRequest reportRequest) {
		try {
			if (reportRequest.getReportType() == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Report type is required")
						.data(null)
						.build();
			}

			if (reportRequest.getSeverity() == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Severity level is required")
						.data(null)
						.build();
			}

			if (reportRequest.getLatitude() == null || reportRequest.getLongitude() == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Location coordinates are required")
						.data(null)
						.build();
			}

			if (reportRequest.getLatitude() < -90 || reportRequest.getLatitude() > 90) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Invalid coordinates. Latitude must be between -90 and 90")
						.data(null)
						.build();
			}

			if (reportRequest.getLongitude() < -180 || reportRequest.getLongitude() > 180) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Invalid coordinates. Longitude must be between -180 and 180")
						.data(null)
						.build();
			}

			if (reportRequest.getTitle() == null || reportRequest.getTitle().trim().isEmpty()) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Title is required")
						.data(null)
						.build();
			}

			if (reportRequest.getDescription() == null || reportRequest.getDescription().trim().isEmpty()) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Description is required")
						.data(null)
						.build();
			}

			String userId = userService.getCurrectUserFromToken().getId();

			Report report = Report.builder()
					.reportType(reportRequest.getReportType())
					.status(ReportStatus.PENDING)
					.severity(reportRequest.getSeverity())
					.title(reportRequest.getTitle())
					.description(reportRequest.getDescription())
					.latitude(reportRequest.getLatitude())
					.longitude(reportRequest.getLongitude())
					.location(reportRequest.getLocation())
					.address(reportRequest.getAddress())
					.userId(userId)
					.deviceInfo(getDeviceInfo())
					.ipAddress(getClientIpAddress())
					.viewCount(0)
					.helpfulCount(0)
					.notHelpfulCount(0)
					.createdDate(LocalDateTime.now())
					.updatedDate(LocalDateTime.now())
					.build();

			Report savedReport = reportRepository.save(report);

			ReportSubmissionEvent event = ReportSubmissionEvent.builder()
					.reportId(savedReport.getId())
					.reportType(savedReport.getReportType())
					.severity(savedReport.getSeverity())
					.title(savedReport.getTitle())
					.description(savedReport.getDescription())
					.latitude(savedReport.getLatitude())
					.longitude(savedReport.getLongitude())
					.address(savedReport.getAddress())
					.userId(savedReport.getUserId())
					.createdAt(savedReport.getCreatedDate())
					.build();

			try {
//				notificationProducer.sendMessage(kafkaTopics.getNotificationTopic(), event, OutboxType.REPORT_SUBMISSION);
			} catch (Exception e) {
				System.err.println("Failed to publish report submission event: " + e.getMessage());
			}

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("reportId", savedReport.getId());
			responseData.put("status", savedReport.getStatus());
			responseData.put("createdAt", savedReport.getCreatedDate());

			return ApiResponse.builder()
					.statusCode(HttpStatus.CREATED.value())
					.message("Report submitted successfully")
					.data(responseData)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error submitting report: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	@Override
	public ApiResponse getReports(PaginationRequest paginationRequest) {
		try {
			int pageNumber = paginationRequest.getPageNumber() != null ? paginationRequest.getPageNumber() : 0;
			int pageSize = paginationRequest.getPageSize() != null ? paginationRequest.getPageSize() : 10;
			String sortBy = paginationRequest.getSortKey() != null ? paginationRequest.getSortKey() : "createdAt";
			String sortDirection = paginationRequest.getSortDirection() != null ? paginationRequest.getSortDirection()
					: "DESC";

			Sort.Direction direction = Sort.Direction.fromString(sortDirection);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

			String userId = userService.getCurrectUserFromToken().getId();

			Page<Report> reports = reportRepository.findByUserId(userId, pageable);

			List<ReportListItemResponse> reportItems = reports.getContent().stream().map(report -> {
				long commentCount = reportCommentRepository.countByReportId(report.getId());
				long attachmentCount = reportAttachmentRepository.countByReportId(report.getId());

				return ReportListItemResponse.builder()
						.id(report.getId())
						.reportType(report.getReportType())
						.status(report.getStatus())
						.severity(report.getSeverity())
						.title(report.getTitle())
						.description(report.getDescription())
						.latitude(report.getLatitude())
						.longitude(report.getLongitude())
						.address(report.getAddress())
						.userId(report.getUserId())
						.createdDate(report.getCreatedDate())
						.updatedDate(report.getUpdatedDate())
						.viewCount(report.getViewCount())
						.helpfulCount(report.getHelpfulCount())
						.notHelpfulCount(report.getNotHelpfulCount())
						.commentCount((int) commentCount)
						.attachmentCount((int) attachmentCount)
						.build();
			}).collect(Collectors.toList());

			PaginatedResponse<ReportListItemResponse> paginatedResponse = PaginatedResponse.<ReportListItemResponse>builder()
					.content(reportItems)
					.pageNumber(reports.getNumber())
					.pageSize(reports.getSize())
					.totalElements(reports.getTotalElements())
					.totalPages(reports.getTotalPages())
					.numberOfElements(reports.getNumberOfElements())
					.firstPage(reports.isFirst())
					.lastPage(reports.isLast())
					.build();

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message("Reports retrieved successfully")
					.data(paginatedResponse)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error retrieving reports: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	@Override
	public ApiResponse getReportById(String reportId) {
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

			if (!report.getUserId().equals(userId)) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.FORBIDDEN.value())
						.message("You do not have permission to access this report")
						.data(null)
						.build();
			}

			report.setViewCount(report.getViewCount() + 1);
			reportRepository.save(report);

			List<ReportComment> comments = reportCommentRepository.findByReportIdOrderByCreatedDateAsc(reportId);
			List<ReportCommentResponse> commentResponses = comments.stream()
				    .map(comment -> ReportCommentResponse.builder()
				        .id(comment.getId())
				        .reportId(comment.getReportId())
				        .userId(comment.getUserId())
				        .comment(comment.getComment())
				        .isAdminComment(comment.getIsAdminComment())
				        .createdDate(comment.getCreatedDate())
				        .updatedDate(comment.getUpdatedDate())
				        .build())
				    .toList();


			List<ReportAttachment> attachments = reportAttachmentRepository.findByReportId(reportId);
			List<ReportAttachmentResponse> attachmentResponses = attachments.stream().map(attachment -> ReportAttachmentResponse
					.builder()
					.id(attachment.getId())
					.fileName(attachment.getFileName())
					.fileUrl(attachment.getFileUrl())
					.fileType(attachment.getFileType())
					.fileSize(attachment.getFileSize())
					.uploadedAt(attachment.getUploadedAt())
					.build()).collect(Collectors.toList());

			ReportResponse reportResponse = ReportResponse.builder()
					.id(report.getId())
					.reportType(report.getReportType())
					.status(report.getStatus())
					.severity(report.getSeverity())
					.title(report.getTitle())
					.description(report.getDescription())
					.latitude(report.getLatitude())
					.longitude(report.getLongitude())
					.address(report.getAddress())
					.userId(report.getUserId())
					.createdDate(report.getCreatedDate())
					.updatedDate(report.getUpdatedDate())
					.resolvedAt(report.getResolvedAt())
					.deviceInfo(report.getDeviceInfo())
					.ipAddress(report.getIpAddress())
					.viewCount(report.getViewCount())
					.helpfulCount(report.getHelpfulCount())
					.notHelpfulCount(report.getNotHelpfulCount())
					.comments(commentResponses)
					.attachments(attachmentResponses)
					.build();

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message("Report retrieved successfully")
					.data(reportResponse)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error retrieving report: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	@Override
	public ApiResponse searchReports(String query, PaginationRequest paginationRequest) {
		try {
			int pageNumber = paginationRequest.getPageNumber() != null ? paginationRequest.getPageNumber() : 0;
			int pageSize = paginationRequest.getPageSize() != null ? paginationRequest.getPageSize() : 10;

			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

			Page<Report> reports = reportRepository.searchByTitleOrDescription(query, pageable);

			List<ReportListItemResponse> reportItems = reports.getContent().stream().map(report -> {
				long commentCount = reportCommentRepository.countByReportId(report.getId());
				long attachmentCount = reportAttachmentRepository.countByReportId(report.getId());

				return ReportListItemResponse.builder()
						.id(report.getId())
						.reportType(report.getReportType())
						.status(report.getStatus())
						.severity(report.getSeverity())
						.title(report.getTitle())
						.description(report.getDescription())
						.latitude(report.getLatitude())
						.longitude(report.getLongitude())
						.address(report.getAddress())
						.userId(report.getUserId())
						.createdDate(report.getCreatedDate())
						.updatedDate(report.getUpdatedDate())
						.viewCount(report.getViewCount())
						.helpfulCount(report.getHelpfulCount())
						.notHelpfulCount(report.getNotHelpfulCount())
						.commentCount((int) commentCount)
						.attachmentCount((int) attachmentCount)
						.build();
			}).collect(Collectors.toList());

			PaginatedResponse<ReportListItemResponse> paginatedResponse = PaginatedResponse.<ReportListItemResponse>builder()
					.content(reportItems)
					.pageNumber(reports.getNumber())
					.pageSize(reports.getSize())
					.totalElements(reports.getTotalElements())
					.totalPages(reports.getTotalPages())
					.numberOfElements(reports.getNumberOfElements())
					.firstPage(reports.isFirst())
					.lastPage(reports.isLast())
					.build();

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message("Search results retrieved successfully")
					.data(paginatedResponse)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error searching reports: " + e.getMessage())
					.data(null)
					.build();
		}
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

	public ApiResponse uploadAttachments(String reportId, MultipartFile[] files) {
		try {
			Report report = reportRepository.findById(reportId).orElse(null);
			if (report == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.NOT_FOUND.value())
						.message("Report not found")
						.data(null)
						.build();
			}

			if (files == null || files.length == 0) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("No files provided")
						.data(null)
						.build();
			}

			long currentAttachmentCount = reportAttachmentRepository.countByReportId(reportId);
			if (currentAttachmentCount + files.length > MAX_ATTACHMENTS) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("Maximum " + MAX_ATTACHMENTS + " attachments allowed per report")
						.data(null)
						.build();
			}

			List<ReportAttachmentResponse> uploadedAttachments = new ArrayList<>();

			for (MultipartFile file : files) {
				if (!isAllowedFileType(file.getContentType())) {
					return ApiResponse.builder()
							.statusCode(HttpStatus.BAD_REQUEST.value())
							.message("File type not allowed. Supported formats: jpg, png, gif")
							.data(null)
							.build();
				}

				if (file.getSize() > MAX_FILE_SIZE) {
					return ApiResponse.builder()
							.statusCode(HttpStatus.BAD_REQUEST.value())
							.message("File size exceeds maximum limit of 5MB")
							.data(null)
							.build();
				}

				String fileUrl = cloudinaryService.uploadFile(file, "reports/" + reportId);
				if (fileUrl == null || fileUrl.isEmpty()) {
					return ApiResponse.builder()
							.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
							.message("Failed to upload file: " + file.getOriginalFilename())
							.data(null)
							.build();
				}

				String publicId = extractPublicIdFromUrl(fileUrl);

				ReportAttachment attachment = ReportAttachment.builder()
						.reportId(reportId)
						.fileName(file.getOriginalFilename())
						.fileUrl(fileUrl)
						.fileType(file.getContentType())
						.fileSize(file.getSize())
						.uploadedAt(LocalDateTime.now())
						.cloudinaryPublicId(publicId)
						.build();

				ReportAttachment savedAttachment = reportAttachmentRepository.save(attachment);

				uploadedAttachments.add(ReportAttachmentResponse.builder()
						.id(savedAttachment.getId())
						.fileName(savedAttachment.getFileName())
						.fileUrl(savedAttachment.getFileUrl())
						.fileType(savedAttachment.getFileType())
						.fileSize(savedAttachment.getFileSize())
						.uploadedAt(savedAttachment.getUploadedAt())
						.build());
			}

			return ApiResponse.builder()
					.statusCode(HttpStatus.CREATED.value())
					.message("Attachments uploaded successfully")
					.data(uploadedAttachments)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error uploading attachments: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	public ApiResponse deleteAttachment(String reportId, String attachmentId) {
		try {
			Report report = reportRepository.findById(reportId).orElse(null);
			if (report == null) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.NOT_FOUND.value())
						.message("Report not found")
						.data(null)
						.build();
			}

			ReportAttachment attachment = reportAttachmentRepository.findById(attachmentId).orElse(null);
			if (attachment == null || !attachment.getReportId().equals(reportId)) {
				return ApiResponse.builder()
						.statusCode(HttpStatus.NOT_FOUND.value())
						.message("Attachment not found")
						.data(null)
						.build();
			}

			if (attachment.getCloudinaryPublicId() != null && !attachment.getCloudinaryPublicId().isEmpty()) {
				cloudinaryService.deleteImage(attachment.getCloudinaryPublicId());
			}

			reportAttachmentRepository.deleteById(attachmentId);

			return ApiResponse.builder()
					.statusCode(HttpStatus.OK.value())
					.message("Attachment deleted successfully")
					.data(null)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder()
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.message("Error deleting attachment: " + e.getMessage())
					.data(null)
					.build();
		}
	}

	private boolean isAllowedFileType(String contentType) {
		if (contentType == null) {
			return false;
		}
		for (String allowedType : ALLOWED_FILE_TYPES) {
			if (contentType.equals(allowedType)) {
				return true;
			}
		}
		return false;
	}

	private String extractPublicIdFromUrl(String fileUrl) {
		if (fileUrl == null || fileUrl.isEmpty()) {
			return "";
		}
		String[] parts = fileUrl.split("/");
		if (parts.length > 0) {
			String lastPart = parts[parts.length - 1];
			return lastPart.contains(".") ? lastPart.substring(0, lastPart.lastIndexOf(".")) : lastPart;
		}
		return "";
	}

	private String getDeviceInfo() {
		return "Unknown Device";
	}

	private String getClientIpAddress() {
		return "0.0.0.0";
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



	@Override
	public ApiResponse searchReportsWithinRadius(Double latitude, Double longitude, Double radiusInKm, PaginationRequest paginationRequest) {
//		try {
//			if (latitude == null || longitude == null) {
//				return ApiResponse.builder()
//						.statusCode(HttpStatus.BAD_REQUEST.value())
//						.message("Latitude and longitude are required")
//						.data(null)
//						.build();
//			}
//
//			if (latitude < -90 || latitude > 90) {
//				return ApiResponse.builder()
//						.statusCode(HttpStatus.BAD_REQUEST.value())
//						.message("Invalid latitude. Must be between -90 and 90")
//						.data(null)
//						.build();
//			}
//
//			if (longitude < -180 || longitude > 180) {
//				return ApiResponse.builder()
//						.statusCode(HttpStatus.BAD_REQUEST.value())
//						.message("Invalid longitude. Must be between -180 and 180")
//						.data(null)
//						.build();
//			}
//
//			if (radiusInKm == null || radiusInKm <= 0) {
//				radiusInKm = 50.0;
//			}
//
//			int pageNumber = paginationRequest.getPageNumber() != null ? paginationRequest.getPageNumber() : 0;
//			int pageSize = paginationRequest.getPageSize() != null ? paginationRequest.getPageSize() : 10;
//
//			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//			double radiusInMeters = radiusInKm * 1000;
//
//			Page<Report> reports = reportRepository.findReportsWithinRadius(latitude, longitude, radiusInMeters, pageable);
//
//			List<ReportListItemResponse> reportItems = reports.getContent().stream().map(report -> {
//				long commentCount = reportCommentRepository.countByReportId(report.getId());
//				long attachmentCount = reportAttachmentRepository.countByReportId(report.getId());
//
//				return ReportListItemResponse.builder()
//						.id(report.getId())
//						.reportType(report.getReportType())
//						.status(report.getStatus())
//						.severity(report.getSeverity())
//						.title(report.getTitle())
//						.description(report.getDescription())
//						.latitude(report.getLatitude())
//						.longitude(report.getLongitude())
//						.address(report.getAddress())
//						.userId(report.getUserId())
//						.createdDate(report.getCreatedDate())
//						.updatedDate(report.getUpdatedDate())
//						.viewCount(report.getViewCount())
//						.helpfulCount(report.getHelpfulCount())
//						.notHelpfulCount(report.getNotHelpfulCount())
//						.commentCount((int) commentCount)
//						.attachmentCount((int) attachmentCount)
//						.build();
//			}).collect(Collectors.toList());
//
//			PaginatedResponse<ReportListItemResponse> paginatedResponse = PaginatedResponse.<ReportListItemResponse>builder()
//					.content(reportItems)
//					.pageNumber(reports.getNumber())
//					.pageSize(reports.getSize())
//					.totalElements(reports.getTotalElements())
//					.totalPages(reports.getTotalPages())
//					.numberOfElements(reports.getNumberOfElements())
//					.firstPage(reports.isFirst())
//					.lastPage(reports.isLast())
//					.build();
//
//			return ApiResponse.builder()
//					.statusCode(HttpStatus.OK.value())
//					.message("Geospatial search results retrieved successfully")
//					.data(paginatedResponse)
//					.build();
//
//		} catch (Exception e) {
//			return ApiResponse.builder()
//					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//					.message("Error searching reports by location: " + e.getMessage())
//					.data(null)
//					.build();
//		}
		
		return null;
	}
}