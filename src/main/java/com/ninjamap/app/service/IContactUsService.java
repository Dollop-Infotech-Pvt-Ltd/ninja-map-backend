package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.ContactUsRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.ContactUsResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IContactUsService {

	ResponseEntity<ApiResponse> saveContact(ContactUsRequest request);

	ResponseEntity<ApiResponse> getContactById(String id);

	ResponseEntity<ApiResponse> deleteContact(String id);

	ResponseEntity<PaginatedResponse<ContactUsResponse>> getAllContacts(PaginationRequest paginationRequest);
}
