package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.payload.request.CreateBusinessRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BusinessResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.service.IBusinessService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/businesses")
@Validated
public class BusinessController {

	@Autowired
	private IBusinessService businessService;

	@PostMapping("create-business")
	public ResponseEntity<ApiResponse> createBusiness(
			CreateBusinessRequest request) {
		BusinessResponse businessResponse = businessService.createBusiness(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.builder()
						.success(true)
						.message("Business created successfully")
						.http(HttpStatus.CREATED)
						.statusCode(HttpStatus.CREATED.value())
						.data(businessResponse)
						.build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse> getBusinessById(@PathVariable String id) {
		BusinessResponse businessResponse = businessService.getBusinessById(id);

		return ResponseEntity.ok(ApiResponse.builder()
				.success(true)
				.message("Business retrieved successfully")
				.http(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.data(businessResponse)
				.build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse> updateBusiness(
			@PathVariable String id,
			@Valid @RequestPart("business") CreateBusinessRequest request,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {

		BusinessResponse businessResponse = businessService.updateBusiness(id, request, images);

		return ResponseEntity.ok(ApiResponse.builder()
				.success(true)
				.message("Business updated successfully")
				.http(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.data(businessResponse)
				.build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> deleteBusiness(@PathVariable String id) {
		businessService.deleteBusiness(id);

		return ResponseEntity.ok(ApiResponse.builder()
				.success(true)
				.message("Business deleted successfully")
				.http(HttpStatus.OK)
				.statusCode(HttpStatus.OK.value())
				.build());
	}

	@GetMapping
	public ResponseEntity<ApiResponse> getAllBusinesses(
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {

//		PaginatedResponse<BusinessResponse> response = businessService.getAllBusinesses(pageIndex, pageSize);

		return null;
	}
}
