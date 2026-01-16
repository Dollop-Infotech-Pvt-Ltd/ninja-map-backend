package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.model.Admin;
import com.ninjamap.app.payload.request.AdminRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.UpdateAdminRequest;
import com.ninjamap.app.payload.response.AdminResponse;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IAdminService {

	public ResponseEntity<ApiResponse> create(AdminRequest request);

	public Admin getAdminByEmailAndIsActive(String email, Boolean isActive);

	public Admin saveAdmin(Admin admin);

	public ResponseEntity<ApiResponse> getById(String id, Boolean isActive);

	public ResponseEntity<PaginatedResponse<AdminResponse>> getAllAdmins(PaginationRequest paginationRequest);

	public ResponseEntity<ApiResponse> update(UpdateAdminRequest request);

	public ResponseEntity<ApiResponse> delete(String id);

	public ResponseEntity<ApiResponse> updateStatus(String id, Boolean isActive);

	public AdminResponse getCurrectAdminFromToken();

	public Admin getAdminByIdAndIsActive(String id, Boolean isActive);

}
