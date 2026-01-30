# Design Document: Report Status Management

## Overview

The Report Status Management feature provides a complete lifecycle management system for reports. It enables moderators to transition reports through defined statuses, enforces valid state transitions, and maintains an audit trail of all changes. The design integrates with the existing ReportController, IReportService, and database layer.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ReportController                         │
│  - updateReportStatus(id, statusUpdateRequest)              │
│  - getReports(filters including status)                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    IReportService                           │
│  - updateReportStatus(reportId, newStatus, userId)          │
│  - getReportsByStatus(status, paginationRequest)            │
│  - validateStatusTransition(currentStatus, newStatus)       │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    IReportRepository                        │
│  - findByStatus(status, pageable)                           │
│  - save(report)                                             │
│  - findById(id)                                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database (Report Entity)                 │
│  - id, status, updatedAt, updatedBy                         │
└─────────────────────────────────────────────────────────────┘
```

## Components and Interfaces

### 1. StatusUpdateRequest (Request DTO)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusUpdateRequest {
    @NotNull(message = "Status cannot be null")
    private ReportStatus newStatus;
}
```

### 2. StatusTransitionValidator

Validates status transitions according to business rules:
- PENDING → UNDER_REVIEW, REJECTED
- UNDER_REVIEW → RESOLVED, REJECTED
- RESOLVED → ARCHIVED
- REJECTED → ARCHIVED
- ARCHIVED → (terminal state, no transitions allowed)

### 3. Report Entity Enhancement

Add fields to existing Report entity:
- `updatedAt: LocalDateTime` - timestamp of last update
- `updatedBy: String` - user ID of last modifier
- `statusHistory: List<StatusChangeLog>` (optional, for audit trail)

### 4. StatusChangeLog Entity (Optional)

For maintaining complete audit trail:
- `id: String`
- `reportId: String`
- `previousStatus: ReportStatus`
- `newStatus: ReportStatus`
- `changedBy: String`
- `changedAt: LocalDateTime`

## Data Models

### Report Entity (Enhanced)

```
Report {
  id: String (Primary Key)
  reportType: ReportType
  status: ReportStatus (indexed for filtering)
  severity: ReportSeverity
  title: String
  description: String
  latitude: Double
  longitude: Double
  address: String
  userId: String
  createdDate: LocalDateTime
  updatedDate: LocalDateTime (NEW)
  updatedBy: String (NEW)
  resolvedAt: LocalDateTime
  ... (existing fields)
}
```

### StatusChangeLog Entity (Optional)

```
StatusChangeLog {
  id: String (Primary Key)
  reportId: String (Foreign Key to Report)
  previousStatus: ReportStatus
  newStatus: ReportStatus
  changedBy: String
  changedAt: LocalDateTime
  reason: String
}
```

## Error Handling

| Scenario | HTTP Status | Error Response |
|----------|------------|-----------------|
| Invalid report ID | 404 | `{error: "Report not found"}` |
| Invalid status value | 400 | `{error: "Invalid status value"}` |
| Unauthorized status update | 403 | `{error: "Insufficient permissions"}` |
| Invalid status transition | 400 | `{error: "Cannot transition from {current} to {new}"}` |
| Missing required fields | 400 | `{error: "Status is required"}` |

## Testing Strategy

### Unit Tests
- StatusTransitionValidator: Test all valid and invalid transitions
- Service layer: Test status update logic with mocked repository
- Controller: Test request validation and response formatting

### Integration Tests
- End-to-end status update flow with database
- Pagination with status filters
- Authorization checks for status updates
- Audit trail recording (if implemented)

### Test Coverage Focus
- Valid status transitions
- Invalid transition rejection
- Authorization enforcement
- Pagination with status filters
- Error response formatting
