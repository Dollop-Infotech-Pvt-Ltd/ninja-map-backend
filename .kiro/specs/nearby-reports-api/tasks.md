# Implementation Plan

- [x] 1. Add repository method for nearby reports query




  - Add native query method to IReportRepository: `findNearbyReports(Double latitude, Double longitude, Integer limit, Pageable pageable)`
  - Implement Haversine formula in SQL to calculate distance between coordinates
  - Sort results by distance ascending, then by createdDate descending
  - _Requirements: 1.1, 1.2, 3.1, 3.2_

- [x] 2. Extend IReportService interface with location-based query method




  - Add method signature: `ApiResponse getReportsByLocation(Double latitude, Double longitude, Integer limit, PaginationRequest paginationRequest, String status, String severity)`
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 3. Implement location-based query in ReportServiceImpl





  - Implement parameter validation (latitude -90 to 90, longitude -180 to 180, limit 1 to 100)
  - Build PaginationRequest with default sorting by createdDate DESC
  - Call repository method with validated parameters
  - Convert Report entities to ReportListItemResponse DTOs using existing mapper
  - Handle exceptions and return appropriate ApiResponse with error messages
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3_

- [x] 4. Add controller endpoint for nearby reports





  - Add GET endpoint: `/api/reports/nearby`
  - Add request parameters: latitude (required), longitude (required), limit (optional, default 20), pageSize (optional, default 10), pageNumber (optional, default 0), status (optional), severity (optional)
  - Call reportService.getReportsByLocation() with parameters
  - Return ResponseEntity with ApiResponse and HTTP 200 status
  - _Requirements: 1.1, 1.2, 1.3, 2.1_

- [ ]* 5. Write integration tests for nearby reports functionality
  - Create test data with reports at various geographic locations
  - Test nearby reports query returns results sorted by distance and creation date
  - Test pagination works correctly with nearby reports
  - Test optional filters (status, severity) work with location query
  - Test validation of invalid coordinates and limit values
  - Test empty results when no reports exist near location
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 3.1, 3.2_
