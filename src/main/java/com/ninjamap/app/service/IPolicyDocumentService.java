package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.enums.DocumentType;
import com.ninjamap.app.payload.request.CreatePolicyDocumentRequest;
import com.ninjamap.app.payload.request.UpdatePolicyDocumentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PolicyDocumentResponse;

public interface IPolicyDocumentService {

	/**
	 * Create a new Policy Document or Terms & Conditions
	 */
	ResponseEntity<ApiResponse> createPolicyDocument(CreatePolicyDocumentRequest request);

	/**
	 * Update an existing Policy Document
	 */
	ResponseEntity<ApiResponse> updatePolicyDocument(UpdatePolicyDocumentRequest request);

	/**
	 * Delete a Policy Document (soft delete)
	 */
	ResponseEntity<ApiResponse> deletePolicyDocument(String id);

	/**
	 * Get Policy Document by ID
	 */
	ResponseEntity<ApiResponse> getPolicyDocument(String id);

	/**
	 * Get paginated list of Policy Documents with optional filtering
	 */
//	PaginatedResponse<PolicyDocumentResponse> getAllPolicyDocuments(DocumentType documentType,
//			PaginationRequest paginationRequest);
	ResponseEntity<List<PolicyDocumentResponse>> getAllPolicyDocuments(DocumentType documentType, String searchValue);

	/**
	 * Update the active status of a Policy Document
	 */
	ResponseEntity<ApiResponse> updateStatus(String id, Boolean isActive);

}
