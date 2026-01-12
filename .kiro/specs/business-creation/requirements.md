# Requirements Document - Business Creation API

## Introduction

The Business Creation feature enables users to register and create a new business profile in the NinjaMap application. This feature allows businesses to provide comprehensive information including their name, category, location details, contact information, operating hours, and business images. The system manages business hours with support for full-time operations and specific opening/closing times for each day of the week.

## Glossary

- **Business**: A registered business entity with complete profile information
- **SubCategory**: A subcategory classification that belongs to a main Category
- **Business Hours**: Operating hours for a business on specific days of the week
- **Weekday**: Days of the week represented as an enumeration (SUNDAY through SATURDAY)
- **Full-Time Operation**: A business that operates 24 hours on a specific day
- **Business Image**: A photograph or image associated with the business (maximum 10 images)
- **Nigeria Phone Number**: A phone number following Nigeria's phone numbering format
- **Geolocation**: Latitude and longitude coordinates representing the business location
- **API Response**: Standardized response object containing success status, message, HTTP status, and data

## Requirements

### Requirement 1: Business Profile Creation

**User Story:** As a business owner, I want to create a business profile with essential information, so that my business can be registered in the system.

#### Acceptance Criteria

1. WHEN a user submits a business creation request, THE system SHALL validate that the business name is provided and not blank
2. WHEN a user submits a business creation request, THE system SHALL validate that the business name is between 1 and 255 characters
3. WHEN a user submits a business creation request, THE system SHALL validate that a SubCategory is selected and provided
4. WHEN a user submits a business creation request, THE system SHALL validate that the address is provided and not blank
5. WHEN a user submits a business creation request, THE system SHALL validate that the address is between 1 and 500 characters

### Requirement 2: Business Location Information

**User Story:** As a business owner, I want to provide my business location using coordinates, so that customers can find me on the map.

#### Acceptance Criteria

1. WHEN a user submits a business creation request, THE system SHALL validate that latitude is provided
2. WHEN a user submits a business creation request, THE system SHALL validate that latitude is between -90 and 90 degrees
3. WHEN a user submits a business creation request, THE system SHALL validate that longitude is provided
4. WHEN a user submits a business creation request, THE system SHALL validate that longitude is between -180 and 180 degrees

### Requirement 3: Business Contact Information

**User Story:** As a business owner, I want to provide contact details for my business, so that customers can reach me.

#### Acceptance Criteria

1. WHEN a user submits a business creation request, THE system SHALL validate that the phone number follows Nigeria's phone numbering format
2. WHEN a user submits a business creation request, THE system SHALL validate that the phone number is provided and not blank
3. WHEN a user submits a business creation request, THE system SHALL accept an optional website URL
4. WHEN a user submits a business creation request, THE system SHALL validate that the website URL format is valid if provided

### Requirement 4: Business Operating Hours

**User Story:** As a business owner, I want to specify my business operating hours for each day of the week, so that customers know when I am open.

#### Acceptance Criteria

1. WHEN a user submits business hours, THE system SHALL support all seven days of the week (SUNDAY through SATURDAY)
2. WHEN a user specifies business hours for a day, THE system SHALL allow marking the day as "Open 24 Hours"
3. WHEN a user specifies business hours for a day, THE system SHALL allow marking the day as "Closed"
4. WHEN a user specifies business hours for a day with specific times, THE system SHALL validate that opening time is provided
5. WHEN a user specifies business hours for a day with specific times, THE system SHALL validate that closing time is provided
6. WHEN a user specifies business hours for a day with specific times, THE system SHALL validate that opening time is before closing time

### Requirement 5: Business Images

**User Story:** As a business owner, I want to upload multiple images of my business, so that customers can see what my business looks like.

#### Acceptance Criteria

1. WHEN a user submits a business creation request, THE system SHALL accept multiple business images
2. WHEN a user submits business images, THE system SHALL validate that no more than 10 images are uploaded
3. WHEN a user submits business images, THE system SHALL validate that each image file is in a supported format
4. WHEN a user submits business images, THE system SHALL store the image URLs in the business profile

### Requirement 6: Business Creation Response

**User Story:** As a business owner, I want to receive confirmation of my business registration, so that I know my business has been successfully created.

#### Acceptance Criteria

1. WHEN a business is successfully created, THE system SHALL return an HTTP 201 (Created) status code
2. WHEN a business is successfully created, THE system SHALL return the created business object with all details
3. WHEN a business creation fails, THE system SHALL return an appropriate HTTP error status code
4. WHEN a business creation fails, THE system SHALL return a descriptive error message
