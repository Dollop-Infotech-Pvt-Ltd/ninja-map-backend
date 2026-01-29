# Requirements Document

## Introduction

The Nearby Reports API feature enables users to discover the latest reports submitted within a specific geographic area. This feature allows clients to query reports by providing a center point (latitude/longitude) and a search radius, returning paginated results sorted by recency. This is useful for map-based applications where users want to see recent activity in their vicinity.

## Glossary

- **Report System**: The system that manages report submissions, including location data, severity, and status
- **Latitude/Longitude**: Geographic coordinates representing a point on Earth (WGS84 format)
- **Limit**: The maximum number of nearby reports to retrieve in a single query
- **Geospatial Query**: A database query that filters records based on geographic distance calculations
- **Pagination**: The process of dividing results into pages for efficient data retrieval
- **Report Status**: The current state of a report (e.g., OPEN, RESOLVED, CLOSED)
- **Report Severity**: The importance level of a report (e.g., LOW, MEDIUM, HIGH, CRITICAL)

## Requirements

### Requirement 1

**User Story:** As a mobile app user, I want to find recent reports near my current location, so that I can stay informed about incidents in my area.

#### Acceptance Criteria

1. WHEN a client requests reports by providing latitude and longitude, THE Report System SHALL return the latest reports near the specified location
2. WHILE filtering by location, THE Report System SHALL sort results by creation date in descending order (newest first)
3. WHEN retrieving nearby reports, THE Report System SHALL support pagination with configurable page size and page number
4. IF the latitude or longitude values are outside valid bounds, THEN THE Report System SHALL reject the request with a validation error
5. WHERE optional filtering is needed, THE Report System SHALL support filtering by report status and severity

### Requirement 2

**User Story:** As an API consumer, I want consistent response formatting for nearby reports, so that I can reliably parse and display the data.

#### Acceptance Criteria

1. WHEN nearby reports are retrieved successfully, THE Report System SHALL return a paginated response containing report list items with location data
2. THE Report System SHALL include pagination metadata (total count, current page, page size) in the response
3. WHEN a report is included in the nearby results, THE Report System SHALL include all relevant fields: id, type, status, severity, comment, coordinates, address, user ID, creation date, and picture URL
4. IF no reports exist within the search area, THE Report System SHALL return an empty paginated response with zero total count

### Requirement 3

**User Story:** As a performance-conscious developer, I want the nearby reports query to be efficient, so that response times remain acceptable even with large datasets.

#### Acceptance Criteria

1. WHEN querying nearby reports, THE Report System SHALL use geospatial indexing to optimize distance calculations
2. THE Report System SHALL limit the maximum number of results to prevent excessive database load
3. WHEN pagination is applied, THE Report System SHALL retrieve only the requested page of results from the database
