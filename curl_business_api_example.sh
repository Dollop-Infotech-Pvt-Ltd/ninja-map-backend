#!/bin/bash

# Create Business API - Correct curl command
# This script demonstrates the proper way to call the create business API

# Note: Replace the Authorization token with your actual token
# Replace the subCategoryId with an actual ID from your database

curl --location 'http://localhost:7002/api/v1/businesses/create-business' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiVVNFUiIsImlzT3RwVmVyaWZpZWQiOnRydWUsInRva2VuVHlwZSI6IkFDQ0VTU19UT0tFTiIsIm90cFR5cGUiOiJMT0dJTiIsInN1YiI6ImRvbGxvcHRlc3RpbmdAZ21haWwuY29tIiwiaWF0IjoxNzY4MTk5ODM4LCJpc3MiOiJOaW5qYS1NYXAiLCJleHAiOjE3NjgyMDM0Mzh9.ttRwdvDwolPOv45IcjCWKiIA8qBeJLDfHhXh6N79J5ZPL3lFabII7EEEBsAn8CK5' \
--form 'business={
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
}' \
--form 'businessImages=@"/C:/Users/Divyanka Choursia/Downloads/other.svg"'
