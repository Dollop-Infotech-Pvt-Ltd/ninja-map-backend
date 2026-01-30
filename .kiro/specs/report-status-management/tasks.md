# Implementation Plan: Report Status Management

- [x] 1. Create StatusUpdateRequest DTO and StatusTransitionValidator





  - Create StatusUpdateRequest class with newStatus field and validation
  - Implement StatusTransitionValidator to define and validate allowed status transitions
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 2. Enhance Report entity with status tracking fields




  - Add updatedAt and updatedBy fields to Report entity
  - Update Report entity constructors and builders
  - _Requirements: 4.1, 4.2, 4.3_

- [x] 3. Add repository methods for status-based queries





  - Add findByStatus(ReportStatus status, Pageable pageable) method to IReportRepository
  - Implement the method in ReportRepository with proper indexing
  - _Requirements: 2.1, 2.2, 2.3_

- [x] 4. Implement service layer methods for status management










  - Add updateReportStatus(reportId, newStatus, userId) method to IReportService
  - Implement the method with status transition validation and database updates
  - Add getReportsByStatus(status, paginationRequest) method to IReportService
  - Implement the method with pagination support
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3_

- [x] 5. Add controller endpoints for status management





  - Add PUT endpoint /api/reports/{id}/status to update report status
  - Add GET endpoint /api/reports/filter/status to retrieve reports by status
  - Implement authorization checks for status update endpoint
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4_

- [ ]* 6. Write unit tests for status management
  - Test StatusTransitionValidator with valid and invalid transitions
  - Test service layer status update logic
  - Test repository status filtering
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4_
