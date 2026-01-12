# Implementation Plan - Business Creation API

- [x] 1. Create Weekday enum and Business-related models


  - Create Weekday enum with SUNDAY through SATURDAY values
  - Create Business entity extending AuditData with all required fields and relationships
  - Create BusinessHours entity extending AuditData with weekday, time fields, and flags
  - Create BusinessImage entity extending AuditData with image URL and display order
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4_

- [x] 2. Create request and response DTOs


  - Create CreateBusinessRequest DTO with validation annotations for all fields
  - Create BusinessHoursRequest DTO with validation for time fields
  - Create BusinessResponse DTO with nested BusinessHoursResponse and BusinessImageResponse
  - Create BusinessHoursResponse DTO
  - Create BusinessImageResponse DTO
  - Add Nigeria phone number validation pattern to ValidationConstants
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4_

- [x] 3. Create repository interfaces


  - Create IBusinessRepository extending JpaRepository with custom query methods
  - Create IBusinessHoursRepository extending JpaRepository with custom query methods
  - Create IBusinessImageRepository extending JpaRepository with custom query methods
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4_

- [x] 4. Create service interfaces and implementations


  - Create IBusinessService interface with CRUD and business-specific methods
  - Create BusinessServiceImpl implementing IBusinessService with validation logic
  - Create IBusinessHoursService interface for hours management
  - Create BusinessHoursServiceImpl implementing IBusinessHoursService
  - Implement business hours validation (opening time before closing time, 24-hour and closed flags)
  - Implement phone number validation for Nigeria format
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4_



- [ ] 5. Implement image upload handling
  - Create image upload logic in BusinessServiceImpl to handle MultipartFile list
  - Integrate with existing ICloudinaryService for image storage
  - Validate image count (maximum 10 images)
  - Validate image file formats
  - Create BusinessImage entities with URLs and display order


  - _Requirements: 5.1, 5.2, 5.3, 5.4_

- [ ] 6. Create BusinessController with API endpoints
  - Create BusinessController with @RestController annotation
  - Implement POST /api/v1/businesses endpoint for business creation
  - Implement GET /api/v1/businesses/{id} endpoint for retrieving business
  - Implement PUT /api/v1/businesses/{id} endpoint for updating business
  - Implement DELETE /api/v1/businesses/{id} endpoint for deleting business



  - Implement GET /api/v1/businesses endpoint for listing all businesses with pagination
  - Return ApiResponse with appropriate HTTP status codes (201 for creation, 200 for success, 4xx/5xx for errors)
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 7. Create database migration scripts
  - Create Flyway migration script for business table
  - Create Flyway migration script for business_hours table
  - Create Flyway migration script for business_image table
  - Add foreign key constraints to SubCategory
  - Add indexes on frequently queried columns (business_id, phone_number, sub_category_id)
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4_

- [ ]* 8. Write integration tests
  - Create test class for business creation API endpoint
  - Write test for successful business creation with all fields
  - Write test for business creation with optional fields
  - Write test for business hours validation
  - Write test for image upload validation (max 10 images)
  - Write test for phone number validation
  - Write test for duplicate phone number handling
  - Write test for SubCategory binding
  - Write test for error responses (400, 404, 409, 500)
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4_
