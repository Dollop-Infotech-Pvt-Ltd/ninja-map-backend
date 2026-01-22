package com.ninjamap.app.controller;

import org.apache.kafka.clients.admin.internals.ApiRequestScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.AdminRequest;
import com.ninjamap.app.payload.request.ChangePasswordRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateAdminProfileRequest;
import com.ninjamap.app.payload.request.UpdateAdminRequest;
import com.ninjamap.app.payload.response.AdminResponse;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@Validated
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    // ========================= CREATE ADMIN =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.CREATE_ADMINS')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid AdminRequest request) {
        return adminService.create(request);
    }

    // ========================= GET ADMIN BY ID =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.VIEW_ADMINS')")
    @GetMapping("/get")
    public ResponseEntity<?> getAdminById(
            @RequestParam(name = AppConstants.ID) 
            @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
            @RequestParam(name = AppConstants.IS_ACTIVE, required = false) Boolean isActive) {
        return adminService.getById(id, isActive);
    }

    // ========================= GET ALL ADMINS =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.VIEW_ADMINS')")
    @GetMapping("/get-all")
    public ResponseEntity<PaginatedResponse<AdminResponse>> getAllAdmins(
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

        return adminService.getAllAdmins(paginationRequest);
    }

    // ========================= UPDATE ADMIN =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.EDIT_ADMINS')")
    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(@Valid UpdateAdminRequest request) {
        return adminService.update(request);
    }

    // ========================= DELETE ADMIN =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.DELETE_ADMINS')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAdmin(
            @RequestParam(name = AppConstants.ID) 
            @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id) {
        return adminService.delete(id);
    }

    // ========================= UPDATE ACTIVE STATUS =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.EDIT_ADMINS')")
    @PatchMapping("/update-isActive-status")
    public ResponseEntity<?> updateStatus(
            @RequestParam(name = AppConstants.ID) 
            @UUIDValidator(message = ValidationConstants.INVALID_UUID) String id,
            @RequestParam(name = AppConstants.IS_ACTIVE) Boolean isActive) {
        return adminService.updateStatus(id, isActive);
    }

    // ========================= GET LOGGED-IN ADMIN =========================
    @PreAuthorize("hasAuthority('ADMIN_MANAGEMENT.VIEW_ADMINS')")
    @GetMapping("/get-loggedIn-user")
    public ResponseEntity<?> getLoggedInAdmin() {
        return ResponseEntity.ok(adminService.getCurrectAdminFromToken());
    }
    
    @PutMapping("/update-admin-profile")
    public ResponseEntity<?> updateProfile(@Valid UpdateAdminProfileRequest updateAdminProfileRequest){
    	 return ResponseEntity.ok(adminService.updateProfile(updateAdminProfileRequest));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
    	 return ResponseEntity.ok(adminService.changePassword(changePasswordRequest));
    }
}
