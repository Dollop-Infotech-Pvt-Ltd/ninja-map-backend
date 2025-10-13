package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.FAQRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.FAQResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IFAQService {
	ResponseEntity<ApiResponse> createFAQ(FAQRequest request);

	ResponseEntity<ApiResponse> getFAQById(String id);

	ResponseEntity<PaginatedResponse<FAQResponse>> getAllFAQs(PaginationRequest paginationRequest);

	ResponseEntity<ApiResponse> updateFAQ(String id, FAQRequest request);

	ResponseEntity<ApiResponse> deleteFAQ(String id);
}
