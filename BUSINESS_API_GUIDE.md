# Business Creation API - Integration Guide

## Overview
The Business Creation API allows you to register and manage businesses with comprehensive details including location, contact information, operating hours, and images.

## API Endpoint
- **URL**: `http://localhost:7002/api/v1/businesses/create-business`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`

## Request Structure

### Form Data Parts

#### 1. Business Details (JSON)
**Field Name**: `business`
**Type**: JSON string

```json
{
  "businessName": "Dollop Cafe",
  "subCategoryId": "58f67f44-8793-45f4-b90c-a1f40bee2ac7",
  "address": "12, MG Road, Indore, MP, India",
  "latitude": 22.7196,
  "longitude": 75.8577,
  "phoneNumber": "+919876543212",
  "website": "https://www.dollopcafe.com",
  "businessHours": [
    {
      "weekday": "SUNDAY",
      "isOpen24Hours": false,
      "isClosed": true,
      "openingTime": null,
      "closingTime": null
    },
    {
      "weekday": "MONDAY",
      "isOpen24Hours": false,
      "isClosed": false,
      "openingTime": "09:00",
      "closingTime": "22:00"
    },
    {
      "weekday": "TUESDAY",
      "isOpen24Hours": false,
      "isClosed": false,
      "openingTime": "09:00",
      "closingTime": "22:00"
    },
    {
      "weekday": "WEDNESDAY",
      "isOpen24Hours": false,
      "isClosed": false,
      "openingTime": "09:00",
      "closingTime": "22:00"
    },
    {
      "weekday": "THURSDAY",
      "isOpen24Hours": false,
      "isClosed": false,
      "openingTime": "09:00",
      "closingTime": "22:00"
    },
    {
      "weekday": "FRIDAY",
      "isOpen24Hours": false,
      "isClosed": false,
      "openingTime": "09:00",
      "closingTime": "23:00"
    },
    {
      "weekday": "SATURDAY",
      "isOpen24Hours": true,
      "isClosed": false,
      "openingTime": null,
      "closingTime": null
    }
  ]
}
```

#### 2. Business Images (Files)
**Field Name**: `businessImages`
**Type**: File(s)
**Max Files**: 10
**Supported Formats**: JPG, PNG, GIF, SVG, WebP

## Field Validation Rules

### Business Name
- **Required**: Yes
- **Type**: String
- **Min Length**: 1
- **Max Length**: 255

### SubCategory ID
- **Required**: Yes
- **Type**: String (UUID)
- **Must exist** in the database

### Address
- **Required**: Yes
- **Type**: String
- **Min Length**: 1
- **Max Length**: 500

### Latitude
- **Required**: Yes
- **Type**: Double
- **Range**: -90 to 90

### Longitude
- **Required**: Yes
- **Type**: Double
- **Range**: -180 to 180

### Phone Number
- **Required**: Yes
- **Type**: String
- **Format**: Nigeria phone number
- **Valid Formats**:
  - `+919876543212` (with country code)
  - `919876543212` (country code without +)
  - `09876543212` (with leading 0)
  - `8012345678` (without leading 0)

### Website
- **Required**: No
- **Type**: String
- **Format**: Valid URL format

### Business Hours
- **Required**: Yes
- **Type**: Array of BusinessHoursRequest
- **Must have**: 7 entries (one for each day of the week)

#### Business Hours Fields
- **weekday**: SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
- **isOpen24Hours**: Boolean (true if open 24 hours)
- **isClosed**: Boolean (true if closed on this day)
- **openingTime**: String in HH:mm format (required if not 24-hour or closed)
- **closingTime**: String in HH:mm format (required if not 24-hour or closed)

## Response Format

### Success Response (201 Created)
```json
{
  "success": true,
  "message": "Business created successfully",
  "http": "CREATED",
  "statusCode": 201,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "businessName": "Dollop Cafe",
    "subCategory": {
      "id": "58f67f44-8793-45f4-b90c-a1f40bee2ac7",
      "subCategoryName": "Cafe"
    },
    "address": "12, MG Road, Indore, MP, India",
    "latitude": 22.7196,
    "longitude": 75.8577,
    "phoneNumber": "+919876543212",
    "website": "https://www.dollopcafe.com",
    "businessHours": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "weekday": "SUNDAY",
        "isOpen24Hours": false,
        "isClosed": true,
        "openingTime": null,
        "closingTime": null
      },
      {
        "id": "550e8400-e29b-41d4-a716-446655440003",
        "weekday": "MONDAY",
        "isOpen24Hours": false,
        "isClosed": false,
        "openingTime": "09:00",
        "closingTime": "22:00"
      }
    ],
    "businessImages": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440004",
        "imageUrl": "https://cloudinary.com/...",
        "displayOrder": 1
      }
    ],
    "createdDate": "2025-01-12T12:30:00",
    "updatedDate": "2025-01-12T12:30:00",
    "isActive": true
  }
}
```

### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation error message",
  "http": "BAD_REQUEST",
  "statusCode": 400
}
```

## Common Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| Business name is required | businessName field is empty | Provide a non-empty business name |
| Business name must be between 1 and 255 characters | businessName exceeds length | Keep name between 1-255 characters |
| Invalid Nigeria phone number format | Phone number doesn't match Nigeria format | Use valid Nigeria phone format |
| Business hours are required | businessHours array is empty or null | Provide hours for all 7 days |
| Invalid business hours configuration | Opening time >= closing time | Ensure opening time < closing time |
| Maximum 10 images allowed | More than 10 images uploaded | Upload maximum 10 images |
| SubCategory not found | subCategoryId doesn't exist | Use valid SubCategory ID |

## Postman Collection

Import the `postman_business_api.json` file into Postman for easy testing.

## cURL Examples

### Basic Example
```bash
curl --location 'http://localhost:7002/api/v1/businesses/create-business' \
--header 'Authorization: Bearer YOUR_TOKEN' \
--form 'business={...json...}' \
--form 'businessImages=@"/path/to/image.jpg"'
```

### With Multiple Images
```bash
curl --location 'http://localhost:7002/api/v1/businesses/create-business' \
--header 'Authorization: Bearer YOUR_TOKEN' \
--form 'business={...json...}' \
--form 'businessImages=@"/path/to/image1.jpg"' \
--form 'businessImages=@"/path/to/image2.jpg"'
```

## Important Notes

1. **Business Hours**: Must provide all 7 days of the week
2. **Phone Number**: Must follow Nigeria phone number format
3. **Images**: Optional but maximum 10 images allowed
4. **SubCategory**: Must exist in the database before creating business
5. **Coordinates**: Use decimal format (e.g., 22.7196 for latitude)
6. **Time Format**: Use 24-hour format (HH:mm)

## Testing Checklist

- [ ] All required fields are provided
- [ ] Phone number is in Nigeria format
- [ ] Business hours include all 7 days
- [ ] Opening time is before closing time
- [ ] Maximum 10 images are uploaded
- [ ] SubCategory ID exists in database
- [ ] Latitude is between -90 and 90
- [ ] Longitude is between -180 and 180
