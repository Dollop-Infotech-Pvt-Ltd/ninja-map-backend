package com.ninjamap.app.service.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.DocumentType;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.PolicyDocument;
import com.ninjamap.app.payload.request.CreatePolicyDocumentRequest;
import com.ninjamap.app.payload.request.UpdatePolicyDocumentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PolicyDocumentResponse;
import com.ninjamap.app.repository.IPolicyDocumentRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IPolicyDocumentService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicyDocumentServiceImpl implements IPolicyDocumentService {

	private final IPolicyDocumentRepository documentRepository;
	private final ICloudinaryService cloudinaryService;
//	private final AppUtils appUtils;

	@Override
	public ResponseEntity<ApiResponse> createPolicyDocument(CreatePolicyDocumentRequest request) {
		// Parse DocumentType
		DocumentType type = AppUtils.parseEnum(DocumentType.class, request.getType().name());

		// Check for duplicate: same type and title
		boolean exists = documentRepository.existsByTypeAndTitleAndIsDeletedFalse(type, request.getTitle());
		if (exists) {
			throw new ResourceAlreadyExistException("A document with this type and title already exists");
		}

		// Build new document
		PolicyDocument document = PolicyDocument.builder().title(request.getTitle())
				.description(request.getDescription()).type(type).build();

		// Handle image upload
		MultipartFile file = request.getDocumentImage();
		if (file != null && !file.isEmpty()) {
			String imageUrl = cloudinaryService.uploadFile(file, "Ninja_Map");
			document.setDocumentImage(imageUrl);
		}

		PolicyDocument saved = documentRepository.save(document);

		ApiResponse response = (saved != null)
				? AppUtils.buildCreatedResponse(AppConstants.POLICY_CREATED_SUCCESS, mapToResponse(saved))
				: AppUtils.buildFailureResponse(AppConstants.POLICY_NOT_CREATED);

		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> updatePolicyDocument(UpdatePolicyDocumentRequest request) {
		// Fetch the existing document or throw 404
		PolicyDocument document = documentRepository.findById(request.getId()).orElseThrow(
				() -> new ResourceNotFoundException("PolicyDocument not found with id: " + request.getId()));

		// Update title if provided
		String newTitle = request.getTitle();
		if (newTitle != null && !newTitle.isBlank() && !newTitle.equals(document.getTitle())) {
			// Check for duplicate with same type and title
			boolean exists = documentRepository.existsByTypeAndTitleAndIsDeletedFalse(document.getType(), newTitle);
			if (exists) {
				throw new ResourceAlreadyExistException("A document with this type and title already exists");
			}
			document.setTitle(newTitle);
		}

		// Update description if provided
		String newDescription = request.getDescription();
		if (newDescription != null && !newDescription.isBlank()) {
			document.setDescription(newDescription);
		}

		PolicyDocument updatedDocument = documentRepository.save(document);

		ApiResponse response = (updatedDocument != null)
				? AppUtils.buildSuccessResponse(AppConstants.POLICY_UPDATED_SUCCESS, mapToResponse(updatedDocument))
				: AppUtils.buildFailureResponse(AppConstants.POLICY_NOT_UPDATED);

		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> deletePolicyDocument(String id) {
		PolicyDocument document = findById(id);

		document.setIsActive(false);
		document.setIsDeleted(true);
		PolicyDocument saved = documentRepository.save(document);

		ApiResponse response = (saved != null)
				? AppUtils.buildSuccessResponse(AppConstants.POLICY_DOCUMENT_DELETED_SUCCESSFULLY)
				: AppUtils.buildFailureResponse(AppConstants.POLICY_NOT_UPDATED);

		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<ApiResponse> updateStatus(String id, Boolean isActive) {
		PolicyDocument document = findById(id);

		if (document.getIsActive() == isActive) {
			ApiResponse response = AppUtils
					.buildFailureResponse(AppConstants.STATUS_ALREADY_SAME + (isActive ? " active" : " inactive"));
			return new ResponseEntity<>(response, response.getHttp());
		}

		document.setIsActive(isActive);
		documentRepository.save(document);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.DOCUMENT_STATUS_UPDATED_SUCCESSFULLY));
	}

	@Override
	public ResponseEntity<ApiResponse> getPolicyDocument(String id) {
		PolicyDocument document = findById(id);
		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(AppConstants.DATA_FATCH_SUCCESSFULLY, mapToResponse(document)));
	}

//	@Override
//	public PaginatedResponse<PolicyDocumentResponse> getAllPolicyDocuments(DocumentType documentType,
//			PaginationRequest paginationRequest) {
//
//		// Build pageable using AppUtils (handles sortKey validation & default)
//		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, PolicyDocument.class);
//
//		// Fetch filtered and paginated results
//		Page<PolicyDocument> page = documentRepository.findAllByFilters(true, documentType, pageable);
//
//		// Map entities to DTOs and wrap in paginated response
//		return new PaginatedResponse<>(page.map(this::mapToResponse));
//	}

	@Override
	public ResponseEntity<List<PolicyDocumentResponse>> getAllPolicyDocuments(DocumentType documentType,
			String searchValue) {
		// Fetch all filtered results
		List<PolicyDocument> documents = documentRepository.findAllByFiltersWithoutPagination(true, documentType);

		// Optional: Apply search filter if searchValue is provided
		if (searchValue != null && !searchValue.isEmpty()) {
			documents = documents.stream()
					.filter(doc -> doc.getTitle().toLowerCase().contains(searchValue.toLowerCase())
							|| doc.getDescription().toLowerCase().contains(searchValue.toLowerCase()))
					.toList();
		}

		// Map entities to DTOs
		return ResponseEntity.ok(documents.stream().map(this::mapToResponse).toList());
	}

	private PolicyDocument findById(String id) {
		return documentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.POLICY_NOT_FOUND));
	}

	private PolicyDocumentResponse mapToResponse(PolicyDocument document) {
		return PolicyDocumentResponse.builder().id(document.getId()).title(document.getTitle())
				.description(document.getDescription()).image(document.getDocumentImage())
				.documentType(document.getType()).build();
	}
}
