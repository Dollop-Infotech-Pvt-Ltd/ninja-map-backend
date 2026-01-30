# Requirements Document: Report Status Management

## Introduction

This feature enables the system to manage report lifecycle through multiple status transitions. Reports can be created in a PENDING state and progress through various statuses (UNDER_REVIEW, RESOLVED, REJECTED, ARCHIVED) based on user actions and system workflows. The feature includes endpoints to update report status, retrieve reports filtered by status, and enforce valid status transitions.

## Glossary

- **Report**: A user-submitted incident or issue containing location, type, severity, and description
- **Report Status**: The current state of a report (PENDING, UNDER_REVIEW, RESOLVED, REJECTED, ARCHIVED)
- **Status Transition**: The change of a report from one status to another
- **Admin/Moderator**: A user with elevated permissions to update report statuses
- **System**: The NinjaMap application backend API

## Requirements

### Requirement 1: Update Report Status

**User Story:** As a moderator, I want to update a report's status so that I can manage the report lifecycle and keep users informed of progress.

#### Acceptance Criteria

1. WHEN a moderator submits a valid status update request with a report ID and new status, THE System SHALL update the report's status in the database and return HTTP 200 with the updated report
2. WHEN a moderator attempts to update a report status without proper authorization, THE System SHALL reject the request and return HTTP 403 Forbidden
3. WHEN a moderator submits a status update with an invalid report ID, THE System SHALL return HTTP 404 Not Found
4. WHEN a moderator submits a status update with an invalid status value, THE System SHALL return HTTP 400 Bad Request with error details

### Requirement 2: Retrieve Reports by Status

**User Story:** As a user, I want to filter reports by status so that I can view reports at specific stages of resolution.

#### Acceptance Criteria

1. WHEN a user requests reports with a specific status filter parameter, THE System SHALL return paginated reports matching that status with HTTP 200
2. WHEN a user requests reports without a status filter, THE System SHALL return all reports regardless of status with HTTP 200
3. WHEN a user requests reports with an invalid status value, THE System SHALL return HTTP 400 Bad Request
4. WHEN a user requests reports with pagination parameters, THE System SHALL apply pagination to status-filtered results

### Requirement 3: Enforce Valid Status Transitions

**User Story:** As a system administrator, I want to enforce valid status transitions so that reports follow a logical workflow and prevent invalid state changes.

#### Acceptance Criteria

1. WHEN a moderator attempts to transition a report from PENDING to UNDER_REVIEW, THE System SHALL allow the transition and update the database
2. WHEN a moderator attempts to transition a report from PENDING directly to ARCHIVED, THE System SHALL reject the transition and return HTTP 400 Bad Request
3. WHEN a moderator attempts to transition a report from RESOLVED back to PENDING, THE System SHALL reject the transition and return HTTP 400 Bad Request
4. WHEN a moderator attempts to transition a report to the same status it currently has, THE System SHALL return HTTP 400 Bad Request with appropriate error message

### Requirement 4: Track Status Update Metadata

**User Story:** As a system auditor, I want to track when and by whom status updates occur so that I can maintain an audit trail of report changes.

#### Acceptance Criteria

1. WHEN a report status is updated, THE System SHALL record the timestamp of the update in the database
2. WHEN a report status is updated, THE System SHALL record the user ID of the moderator who made the update
3. WHEN a user retrieves a report, THE System SHALL include the last updated timestamp in the response
4. WHERE status history tracking is enabled, THE System SHALL maintain a log of all status transitions with timestamps and user IDs
