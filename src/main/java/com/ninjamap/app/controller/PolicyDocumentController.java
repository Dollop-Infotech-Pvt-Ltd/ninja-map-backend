package com.ninjamap.app.controller;

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

import com.ninjamap.app.enums.DocumentType;
import com.ninjamap.app.payload.request.CreatePolicyDocumentRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdatePolicyDocumentRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.PolicyDocumentResponse;
import com.ninjamap.app.service.IPolicyDocumentService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@Validated
public class PolicyDocumentController {

    private final IPolicyDocumentService policyDocumentService;

    // ========================= CREATE POLICY DOCUMENT =========================
    @PreAuthorize("hasAuthority('POLICY_MANAGEMENT.CREATE_POLICY_DOCUMENTS')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid CreatePolicyDocumentRequest request) {
        return policyDocumentService.createPolicyDocument(request);
    }

    // ========================= UPDATE POLICY DOCUMENT =========================
    @PreAuthorize("hasAuthority('POLICY_MANAGEMENT.UPDATE_POLICY_DOCUMENTS')")
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid UpdatePolicyDocumentRequest request) {
        return policyDocumentService.updatePolicyDocument(request);
    }

    // ========================= GET POLICY DOCUMENT BY ID =========================
    @GetMapping("/get")
    public ResponseEntity<?> getById(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return policyDocumentService.getPolicyDocument(id);
    }

    // ========================= GET ALL POLICY DOCUMENTS =========================
    @GetMapping("/get-all")
    public ResponseEntity<PaginatedResponse<PolicyDocumentResponse>> getAll(
            @RequestParam(name = AppConstants.DOCUMENT_TYPE, required = false) DocumentType documentType,
            @RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
            @RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
            @RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortDirection(sortDirection)
                .sortKey(sortKey)
                .searchValue(searchValue)
                .build();

        return policyDocumentService.getAllPolicyDocuments(documentType, paginationRequest);
    }

    // ========================= DELETE POLICY DOCUMENT =========================
    @PreAuthorize("hasAuthority('POLICY_MANAGEMENT.DELETE_POLICY_DOCUMENTS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return policyDocumentService.deletePolicyDocument(id);
    }
}
