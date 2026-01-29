# Design Document

## Overview

The Nearby Reports API provides a geospatial query capability to retrieve the latest reports near a specified location. The design leverages the existing Report entity structure and extends the repository layer with geospatial queries based on latitude and longitude proximity. The implementation follows the established patterns in the codebase: controller → service → repository architecture with pagination support.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ReportController                         │
│  GET /api/reports/nearby?lat=X&lon=Y&limit=Z&page=0...    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    IReportService                           │
│  getReportsByLocation(lat, lon, limit, pagination)         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  IReportRepository                          │
│  findNearbyReports(lat, lon, limit, pageable)              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Report Entity                            │
│  (latitude, longitude, createdDate, status, severity)      │
└─────────────────────────────────────────────────────────────┘
```

## Components and Interfaces

### 1. Controller Layer

**ReportController Enhancement**
- Add new endpoint: `GET /api/reports/nearby`
- Parameters:
  - `latitude` (required, Double): Center point latitude (-90 to 90)
  - `longitude` (required, Double): Center point longitude (-180 to 180)
  - `limit` (optional, Integer): Maximum number of nearby reports to retrieve (default: 20, max: 100)
  - `pageSize` (optional, Integer): Results per page (default: 10)
  - `pageNumber` (optional, Integer): Page number (default: 0)
  - `status` (optional, String): Filter by report status
  - `severity` (optional, String): Filter by report severity
- Returns: `ResponseEntity<ApiResponse>` with paginated `ReportListItemResponse` objects sorted by proximity and recency

### 2. Service Layer

**IReportService Interface Enhancement**
- Add method: `ApiResponse getReportsByLocation(Double latitude, Double longitude, Integer limit, PaginationRequest paginationRequest, String status, String severity)`
- Responsibilities:
  - Validate input parameters (coordinate ranges, limit bounds)
  - Build pagination request with default sorting by createdDate DESC
  - Call repository method to fetch nearby reports
  - Convert Report entities to ReportListItemResponse DTOs
  - Handle exceptions and return appropriate error responses

**ReportServiceImpl Implementation**
- Implement location-based query logic
- Validate latitude (-90 to 90), longitude (-180 to 180), limit (1 to 100)
- Use Haversine formula or database native geospatial functions for distance calculation
- Apply optional status and severity filters
- Sort results by creation date in descending order (newest first)
- Return paginated results using existing PaginatedResponse wrapper

### 3. Repository Layer

**IReportRepository Enhancement**
- Add method: `Page<Report> findNearbyReports(Double latitude, Double longitude, Integer limit, Pageable pageable)`
- Optional method for filtered queries: `Page<Report> findNearbyReportsWithFilters(Double latitude, Double longitude, Integer limit, ReportStatus status, ReportSeverity severity, Pageable pageable)`
- Implementation approach:
  - Use Spring Data JPA with native query or custom JPQL
  - Implement Haversine formula: `6371 * acos(cos(radians(90 - lat1)) * cos(radians(90 - lat2)) + sin(radians(90 - lat1)) * sin(radians(90 - lat2)) * cos(radians(lon1 - lon2)))`
  - Calculate distance for each report and order by distance ascending, then by createdDate descending
  - Limit results to the specified limit parameter
  - Alternative: Use database-specific geospatial functions (PostGIS for PostgreSQL, ST_Distance for MySQL 5.7+)

## Data Models

### Input Model: LocationSearchRequest (Optional DTO)
```
- latitude: Double (required, -90 to 90)
- longitude: Double (required, -180 to 180)
- limit: Integer (optional, 1 to 100, default: 20)
- status: String (optional)
- severity: String (optional)
```

### Output Model: ReportListItemResponse (Existing)
```
- id: String
- reportType: ReportType
- status: ReportStatus
- severity: ReportSeverity
- comment: String
- latitude: Double
- longitude: Double
- address: String
- userId: String
- reportPicture: String
- createdDate: LocalDateTime
- updatedDate: LocalDateTime
```

### Response Wrapper: PaginatedResponse (Existing)
```
- content: List<ReportListItemResponse>
- totalElements: Long
- totalPages: Integer
- currentPage: Integer
- pageSize: Integer
```

## Error Handling

1. **Invalid Coordinates**: Return 400 Bad Request if latitude/longitude outside valid ranges
2. **Invalid Limit**: Return 400 Bad Request if limit < 1 or > 100
3. **Invalid Pagination**: Return 400 Bad Request if pageSize or pageNumber invalid
4. **Invalid Filters**: Return 400 Bad Request if status or severity values are invalid
5. **Database Error**: Return 500 Internal Server Error with generic message
6. **No Results**: Return 200 OK with empty paginated response

## Testing Strategy

### Unit Tests
- Validate coordinate range checks (latitude, longitude)
- Validate radius bounds (0.1 to 50 km)
- Validate pagination parameter handling
- Test filter application (status, severity)
- Test response mapping from Report to ReportListItemResponse

### Integration Tests
- Test geospatial query with sample data at various distances
- Test pagination with multiple pages of results
- Test combined filters (status + severity + location)
- Test sorting by creation date (newest first)
- Test edge cases (reports at exact coordinates, empty results, limit boundaries)

### Performance Considerations
- Ensure database indexes on latitude, longitude columns
- Consider composite index on (latitude, longitude, createdDate)
- Monitor query execution time with large datasets
- Implement query result caching if needed for frequently accessed areas
