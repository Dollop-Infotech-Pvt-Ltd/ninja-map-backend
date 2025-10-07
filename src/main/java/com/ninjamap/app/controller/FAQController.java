package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.FAQRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.FAQResponse;
import com.ninjamap.app.service.IFAQService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
@Validated
public class FAQController {

	private final IFAQService ifaqService;

	@PreAuthorize("hasAuthority('FAQ_MANAGEMENT.CREATE_FAQS')")
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createFAQ(@Valid FAQRequest request) {
		return ifaqService.createFAQ(request);
	}

	@GetMapping("/get-all")
	public ResponseEntity<List<FAQResponse>> getAllFAQs() {
		return ifaqService.getAllFAQs();
	}

	@GetMapping("/get")
	public ResponseEntity<ApiResponse> getFAQById(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return ifaqService.getFAQById(id);
	}

	@PreAuthorize("hasAuthority('FAQ_MANAGEMENT.EDIT_FAQS')")
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateFAQ(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
			@Valid FAQRequest request) {
		return ifaqService.updateFAQ(id, request);
	}

	@PreAuthorize("hasAuthority('FAQ_MANAGEMENT.DELETE_FAQS')")
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteFAQ(
			@RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
		return ifaqService.deleteFAQ(id);
	}
}
