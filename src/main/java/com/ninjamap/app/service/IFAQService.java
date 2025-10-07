package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.FAQRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.FAQResponse;

public interface IFAQService {
	ResponseEntity<ApiResponse> createFAQ(FAQRequest request);

	ResponseEntity<ApiResponse> getFAQById(String id);

	ResponseEntity<List<FAQResponse>> getAllFAQs();

	ResponseEntity<ApiResponse> updateFAQ(String id, FAQRequest request);

	ResponseEntity<ApiResponse> deleteFAQ(String id);
}
