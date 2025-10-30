package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.PermissionRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PermissionResponse;
import com.ninjamap.app.service.IPermissionService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Validated
public class PermissionController {

    private final IPermissionService permissionService;

    // ========================= CREATE PERMISSION =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.CREATE_PERMISSIONS')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPermission(@RequestBody @Valid PermissionRequest request) {
        return permissionService.createPermission(request);
    }

    // ========================= GET ALL PERMISSIONS =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.VIEW_PERMISSIONS')")
    @GetMapping("/get-all")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions(
            @RequestParam(defaultValue = "All") String resource,
            @RequestParam(defaultValue = "All") String type,
            @RequestParam(required = false) String searchKeyword) {

        return permissionService.getAllPermissions(resource, type, searchKeyword);
    }

    // ========================= GET PERMISSION BY ID =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.VIEW_PERMISSIONS')")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getPermissionById(
            @RequestParam(name = AppConstants.PERMISSION_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String permissionId,
            @RequestParam(required = false) Boolean isActive) {

        return permissionService.getPermissionById(permissionId, isActive);
    }

    // ========================= UPDATE PERMISSION =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.EDIT_PERMISSIONS')")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updatePermission(
            @RequestParam(name = AppConstants.PERMISSION_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String permissionId,
            @RequestBody @Valid PermissionRequest request) {

        return permissionService.updatePermission(permissionId, request);
    }

    // ========================= DELETE PERMISSION =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.DELETE_PERMISSIONS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePermission(
            @RequestParam(name = AppConstants.PERMISSION_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String permissionId) {

        return permissionService.deletePermission(permissionId);
    }

    // ========================= GET PERMISSIONS BY ROLE =========================
    @PreAuthorize("hasAuthority('PERMISSION_MANAGEMENT.VIEW_PERMISSIONS')")
    @GetMapping("/get-by-role")
    public ResponseEntity<List<PermissionResponse>> getAllPermissionByRole(
            @RequestParam(name = AppConstants.ROLE_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String roleId,
            @RequestParam(defaultValue = "All") String resource,
            @RequestParam(defaultValue = "All") String type,
            @RequestParam(required = false) String searchKeyword) {

        return permissionService.getAllPermissionByRoleId(roleId, resource, type);
    }
}
