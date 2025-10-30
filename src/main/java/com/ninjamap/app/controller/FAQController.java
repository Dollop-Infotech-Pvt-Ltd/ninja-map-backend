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

import com.ninjamap.app.payload.request.FAQRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.FAQResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
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

    // ========================= CREATE FAQ =========================
    @PreAuthorize("hasAuthority('FAQ_MANAGEMENT.CREATE_FAQS')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createFAQ(@Valid FAQRequest request) {
        return ifaqService.createFAQ(request);
    }

    // ========================= GET ALL FAQS =========================
    @GetMapping("/get-all")
    public ResponseEntity<PaginatedResponse<FAQResponse>> getAllFAQs(
            @RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
            @RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
            @RequestParam(name = AppConstants.SORT_KEY, defaultValue = "createdAt", required = false) String sortKey,
            @RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .sortDirection(sortDirection)
                .sortKey(sortKey)
                .searchValue(searchValue)
                .build();

        return ifaqService.getAllFAQs(paginationRequest);
    }

    // ========================= GET FAQ BY ID =========================
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getFAQById(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return ifaqService.getFAQById(id);
    }

    // ========================= UPDATE FAQ =========================
    @PreAuthorize("hasAuthority('FAQ_MANAGEMENT.EDIT_FAQS')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateFAQ(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
            @Valid FAQRequest request) {

        return ifaqService.updateFAQ(id, request);
    }

    // ========================= DELETE FAQ =========================
    @PreAuthorize("hasAuthority('FAQ_MANAGEMENT.DELETE_FAQS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteFAQ(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return ifaqService.deleteFAQ(id);
    }
}
