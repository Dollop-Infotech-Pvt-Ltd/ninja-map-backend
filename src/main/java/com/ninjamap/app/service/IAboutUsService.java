package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.AboutUsRequest;
import com.ninjamap.app.payload.request.UpdateAboutUsRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IAboutUsService {

	ResponseEntity<ApiResponse> addAboutUs(AboutUsRequest request);

	ResponseEntity<ApiResponse> getAboutUs(String id);

	ResponseEntity<ApiResponse> updateAboutUs(String id, UpdateAboutUsRequest request);

}
