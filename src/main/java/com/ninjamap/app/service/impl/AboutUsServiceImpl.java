package com.ninjamap.app.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.AboutUs;
import com.ninjamap.app.payload.request.AboutUsRequest;
import com.ninjamap.app.payload.request.UpdateAboutUsRequest;
import com.ninjamap.app.payload.response.AboutUsResponse;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.repository.IAboutUsRepository;
import com.ninjamap.app.service.IAboutUsService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AboutUsServiceImpl implements IAboutUsService {

	private final IAboutUsRepository aboutUsRepository;
	private final AppUtils appUtils;

	// New method to add AboutUs
	@Override
	public ResponseEntity<ApiResponse> addAboutUs(AboutUsRequest request) {
		AboutUs aboutUs = appUtils.convertTo(request, AboutUs.class);

		AboutUs savedAboutUs = aboutUsRepository.save(aboutUs);

		ApiResponse response = (savedAboutUs != null)
				? AppUtils.buildSuccessResponse(AppConstants.ABOUT_US_ADDED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.ABOUT_US_NOT_ADDED);

		// Use generics properly
		return new ResponseEntity<ApiResponse>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> getAboutUs(String id) {
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ABOUT_US_SUCCESSFULLY_GET,
				mapToResponse(getExistingAboutUs(id))));
	}

	@Override
	public ResponseEntity<ApiResponse> updateAboutUs(String id, UpdateAboutUsRequest request) {
		// Find AboutUs by ID
		AboutUs aboutUs = getExistingAboutUs(id);

		// Update content
		aboutUs.setContent(request.getContent() != null ? request.getContent() : aboutUs.getContent());

		// Save updated entity
		AboutUs saved = aboutUsRepository.save(aboutUs);

		// Build response based on whether save was successful
		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.ABOUT_US_UPDATED)
				: AppUtils.buildFailureResponse(AppConstants.ABOUT_US_NOT_UPDATED);
		return new ResponseEntity<>(response, response.getHttp());

	}

	private AboutUs getExistingAboutUs(String id) {
		return aboutUsRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("AboutUs not found with id: " + id));
	}

	private AboutUsResponse mapToResponse(AboutUs aboutUs) {
		return appUtils.convertTo(aboutUs, AboutUsResponse.class);
	}

}
