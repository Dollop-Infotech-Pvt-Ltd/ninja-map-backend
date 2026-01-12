package com.ninjamap.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.payload.request.CreateBusinessRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BusinessResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;

public interface IBusinessService {

	BusinessResponse createBusiness(CreateBusinessRequest request );

	BusinessResponse getBusinessById(String businessId);

	BusinessResponse updateBusiness(String businessId, CreateBusinessRequest request, List<MultipartFile> images);

	void deleteBusiness(String businessId);

	ApiResponse getAllBusinesses(int pageIndex, int pageSize);
}
