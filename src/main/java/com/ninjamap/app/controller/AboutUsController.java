package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.AboutUsRequest;
import com.ninjamap.app.payload.request.UpdateAboutUsRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IAboutUsService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/about-us")
@Validated
@RequiredArgsConstructor
public class AboutUsController {

	private final IAboutUsService aboutUsService;

	@PreAuthorize("hasAuthority('ABOUT_US_MANAGEMENT.CREATE_ABOUT_US')")
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> addAboutUs(@Valid @RequestBody AboutUsRequest request) {
		return aboutUsService.addAboutUs(request);
	}

//	@PreAuthorize("hasAuthority('ABOUT_US_MANAGEMENT.View Roles')")
	@GetMapping("/get")
	public ResponseEntity<?> getAboutUs(
			@RequestParam(name = AppConstants.ID, required = true) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return aboutUsService.getAboutUs(id);
	}

	@PreAuthorize("hasAuthority('ABOUT_US_MANAGEMENT.EDIT_ABOUT_US')")
	@PutMapping("/update")
	public ResponseEntity<?> updateAboutUs(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@Valid @RequestBody UpdateAboutUsRequest request) {
		return aboutUsService.updateAboutUs(id, request);
	}
}
