package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.ContactUsRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.ContactUsResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.service.IContactUsService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contact-us")
@RequiredArgsConstructor
@Validated
public class ContactUsController {

    private final IContactUsService contactUsService;

    // ========================= SUBMIT CONTACT =========================
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse> createContact(@Valid @RequestBody ContactUsRequest request) {
        return contactUsService.saveContact(request);
    }

    // ========================= GET ALL CONTACTS =========================
    @PreAuthorize("hasAuthority('CONTACT_US_MANAGEMENT.VIEW_CONTACT_US')")
    @GetMapping("/get-all")
    public ResponseEntity<PaginatedResponse<ContactUsResponse>> getAllContacts(
            @RequestParam(name = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
            @RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey,
            @RequestParam(name = AppConstants.SEARCH_VALUE, required = false) String searchValue) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .sortDirection(sortDirection)
                .sortKey(sortKey)
                .searchValue(searchValue)
                .build();

        return contactUsService.getAllContacts(paginationRequest);
    }

    // ========================= GET CONTACT BY ID =========================
    @PreAuthorize("hasAuthority('CONTACT_US_MANAGEMENT.VIEW_CONTACT_US')")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getContactById(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return contactUsService.getContactById(id);
    }

    // ========================= DELETE CONTACT =========================
    @PreAuthorize("hasAuthority('CONTACT_US_MANAGEMENT.DELETE_CONTACT_US')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteContact(
            @RequestParam(name = AppConstants.ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return contactUsService.deleteContact(id);
    }
}
