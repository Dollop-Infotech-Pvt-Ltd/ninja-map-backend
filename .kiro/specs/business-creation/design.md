# Design Document - Business Creation API

## Overview

The Business Creation API enables business owners to register their business with comprehensive details including location, contact information, operating hours, and images. The system is built using Spring Boot with JPA/Hibernate for data persistence and follows RESTful API conventions. The design leverages existing patterns in the codebase (PlaceRequest, ApiResponse) and integrates with the existing SubCategory model.

## Architecture

### Layered Architecture
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic and validation
- **Repository Layer**: Manages data persistence
- **Model Layer**: Defines entity structures
- **Payload Layer**: Request/Response DTOs

### Data Flow
1. Client sends POST request with business details and images
2. Controller validates request and passes to service
3. Service validates business data and processes images
4. Repository persists Business, BusinessHours, and BusinessImage entities
5. Service returns created business with all details
6. Controller returns ApiResponse with created business data

## Components and Interfaces

### 1. Models (Entities)

#### Business Entity
```
- id: String (UUID)
- businessName: String (1-255 chars, required)
- subCategory: SubCategory (required, foreign key)
- address: String (1-500 chars, required)
- latitude: Double (-90 to 90, required)
- longitude: Double (-180 to 180, required)
- phoneNumber: String (Nigeria format, required)
- website: String (optional, URL format)
- businessHours: List<BusinessHours> (one-to-many relationship)
- businessImages: List<BusinessImage> (one-to-many relationship, max 10)
- createdDate: LocalDateTime (inherited from AuditData)
- updatedDate: LocalDateTime (inherited from AuditData)
- isActive: Boolean (inherited from AuditData)
- isDeleted: Boolean (inherited from AuditData)
```

#### BusinessHours Entity
```
- id: String (UUID)
- business: Business (foreign key)
- weekday: Weekday (enum: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY)
- isOpen24Hours: Boolean (default: false)
- isClosed: Boolean (default: false)
- openingTime: LocalTime (optional, required if not 24-hour or closed)
- closingTime: LocalTime (optional, required if not 24-hour or closed)
- createdDate: LocalDateTime (inherited from AuditData)
- updatedDate: LocalDateTime (inherited from AuditData)
- isActive: Boolean (inherited from AuditData)
- isDeleted: Boolean (inherited from AuditData)
```

#### BusinessImage Entity
```
- id: String (UUID)
- business: Business (foreign key)
- imageUrl: String (URL of uploaded image)
- createdDate: LocalDateTime (inherited from AuditData)
- updatedDate: LocalDateTime (inherited from AuditData)
- isActive: Boolean (inherited from AuditData)
- isDeleted: Boolean (inherited from AuditData)
```

#### Weekday Enum
```
- SUNDAY
- MONDAY
- TUESDAY
- WEDNESDAY
- THURSDAY
- FRIDAY
- SATURDAY
```

### 2. Request/Response DTOs

#### CreateBusinessRequest
```
- businessName: String (required, 1-255 chars)
- subCategoryId: String (required, UUID)
- address: String (required, 1-500 chars)
- latitude: Double (required, -90 to 90)
- longitude: Double (required, -180 to 180)
- phoneNumber: String (required, Nigeria format)
- website: String (optional, URL format)
- businessHours: List<BusinessHoursRequest> (required, 7 entries for each day)
- businessImages: List<MultipartFile> (optional, max 10 files)
```

#### BusinessHoursRequest
```
- weekday: Weekday (required)
- isOpen24Hours: Boolean (default: false)
- isClosed: Boolean (default: false)
- openingTime: String (optional, format: HH:mm)
- closingTime: String (optional, format: HH:mm)
```

#### BusinessResponse
```
- id: String
- businessName: String
- subCategory: SimpleSubCategoryResponse
- address: String
- latitude: Double
- longitude: Double
- phoneNumber: String
- website: String
- businessHours: List<BusinessHoursResponse>
- businessImages: List<BusinessImageResponse>
- createdDate: LocalDateTime
- updatedDate: LocalDateTime
- isActive: Boolean
```

#### BusinessHoursResponse
```
- id: String
- weekday: Weekday
- isOpen24Hours: Boolean
- isClosed: Boolean
- openingTime: String (HH:mm format)
- closingTime: String (HH:mm format)
```

#### BusinessImageResponse
```
- id: String
- imageUrl: String
- displayOrder: Integer
```

### 3. Service Interfaces

#### IBusinessService
```
- createBusiness(CreateBusinessRequest, List<MultipartFile>): BusinessResponse
- getBusinessById(String): BusinessResponse
- updateBusiness(String, CreateBusinessRequest, List<MultipartFile>): BusinessResponse
- deleteBusiness(String): void
- getAllBusinesses(PaginationRequest): PaginatedResponse<BusinessResponse>
```

#### IBusinessHoursService
```
- createBusinessHours(String, List<BusinessHoursRequest>): List<BusinessHours>
- updateBusinessHours(String, List<BusinessHoursRequest>): List<BusinessHours>
```

### 4. Repository Interfaces

#### IBusinessRepository
```
- extends JpaRepository<Business, String>
- findBySubCategoryId(String): List<Business>
- findByPhoneNumber(String): Optional<Business>
- findAllActive(): List<Business>
```

#### IBusinessHoursRepository
```
- extends JpaRepository<BusinessHours, String>
- findByBusinessId(String): List<BusinessHours>
- findByBusinessIdAndWeekday(String, Weekday): Optional<BusinessHours>
```

#### IBusinessImageRepository
```
- extends JpaRepository<BusinessImage, String>
- findByBusinessId(String): List<BusinessImage>
- deleteByBusinessId(String): void
```

### 5. Controller

#### BusinessController
```
- POST /api/v1/businesses - Create new business
- GET /api/v1/businesses/{id} - Get business by ID
- PUT /api/v1/businesses/{id} - Update business
- DELETE /api/v1/businesses/{id} - Delete business
- GET /api/v1/businesses - Get all businesses (paginated)
```

## Data Models

### Database Schema

**business table**
- id (UUID, PK)
- business_name (VARCHAR 255, NOT NULL)
- sub_category_id (UUID, FK, NOT NULL)
- address (VARCHAR 500, NOT NULL)
- latitude (DECIMAL 10,8, NOT NULL)
- longitude (DECIMAL 11,8, NOT NULL)
- phone_number (VARCHAR 20, NOT NULL)
- website (VARCHAR 255, NULL)
- created_date (DATETIME, NOT NULL)
- updated_date (DATETIME, NOT NULL)
- is_active (BOOLEAN, NOT NULL, DEFAULT TRUE)
- is_deleted (BOOLEAN, NOT NULL, DEFAULT FALSE)

**business_hours table**
- id (UUID, PK)
- business_id (UUID, FK, NOT NULL)
- weekday (ENUM, NOT NULL)
- is_open_24_hours (BOOLEAN, NOT NULL, DEFAULT FALSE)
- is_closed (BOOLEAN, NOT NULL, DEFAULT FALSE)
- opening_time (TIME, NULL)
- closing_time (TIME, NULL)
- created_date (DATETIME, NOT NULL)
- updated_date (DATETIME, NOT NULL)
- is_active (BOOLEAN, NOT NULL, DEFAULT TRUE)
- is_deleted (BOOLEAN, NOT NULL, DEFAULT FALSE)

**business_image table**
- id (UUID, PK)
- business_id (UUID, FK, NOT NULL)
- image_url (VARCHAR 500, NOT NULL)
- display_order (INT, NOT NULL)
- created_date (DATETIME, NOT NULL)
- updated_date (DATETIME, NOT NULL)
- is_active (BOOLEAN, NOT NULL, DEFAULT TRUE)
- is_deleted (BOOLEAN, NOT NULL, DEFAULT FALSE)

## Error Handling

### Validation Errors
- Business name validation: Return 400 with message "Business name is required and must be between 1 and 255 characters"
- SubCategory validation: Return 400 with message "Valid SubCategory is required"
- Location validation: Return 400 with message "Valid latitude and longitude are required"
- Phone number validation: Return 400 with message "Invalid Nigeria phone number format"
- Business hours validation: Return 400 with message "Invalid business hours configuration"
- Image count validation: Return 400 with message "Maximum 10 images allowed"

### Business Logic Errors
- SubCategory not found: Return 404 with message "SubCategory not found"
- Business not found: Return 404 with message "Business not found"
- Duplicate phone number: Return 409 with message "Business with this phone number already exists"

### File Upload Errors
- Invalid file format: Return 400 with message "Invalid image format"
- File size exceeded: Return 413 with message "File size exceeds maximum allowed"
- Upload failure: Return 500 with message "Failed to upload image"

## Testing Strategy

### Unit Tests
- Business model validation
- BusinessHours model validation
- Request DTO validation
- Service layer business logic
- Repository queries

### Integration Tests
- End-to-end business creation flow
- Business hours creation and validation
- Image upload and storage
- Database persistence
- API endpoint responses

### Test Coverage Areas
- Valid business creation with all fields
- Business creation with optional fields
- Business hours for all days of the week
- 24-hour operation flag
- Closed day flag
- Multiple image uploads (up to 10)
- Validation error scenarios
- Duplicate phone number handling
- SubCategory binding
