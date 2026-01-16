package com.ninjamap.app.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Business;
import com.ninjamap.app.model.BusinessHours;
import com.ninjamap.app.model.BusinessImage;
import com.ninjamap.app.model.SubCategory;
import com.ninjamap.app.payload.request.CreateBusinessRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BusinessResponse;
import com.ninjamap.app.repository.IBusinessRepository;
import com.ninjamap.app.repository.ISubCategoryRepository;
import com.ninjamap.app.service.IBusinessHoursService;
import com.ninjamap.app.service.IBusinessService;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

@Service
public class BusinessServiceImpl implements IBusinessService {

	@Autowired
	private IBusinessRepository businessRepository;

	@Autowired
	private ISubCategoryRepository subCategoryRepository;

	@Autowired
	private IBusinessHoursService businessHoursService;

	@Autowired
	private ICloudinaryService cloudinaryService;

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	@Override
	public ApiResponse createBusiness(CreateBusinessRequest request ) {
		SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SUBCATEGORY_NOT_FOUND));

		// Create Business entity
		Business business = Business.builder()
				.businessName(request.getBusinessName())
				.subCategory(subCategory)
				.address(request.getAddress())
				.latitude(request.getLatitude())
				.longitude(request.getLongitude())
				.phoneNumber(request.getPhoneNumber())
				.website(request.getWebsite())
				.build();
		Business savedBusiness = businessRepository.save(business);
		
		// Create business hours
		List<BusinessHours> businessHours = businessHoursService.createBusinessHours(savedBusiness.getId(),
				request.getBusinessHours());
		savedBusiness.setBusinessHours(businessHours);

		// Handle image uploads
		List<BusinessImage> businessImages = new ArrayList<>();
		if (request.getBusinessImages() != null && !request.getBusinessImages().isEmpty()) {
			if (request.getBusinessImages().size() > 10) {
				throw new BadRequestException(ValidationConstants.BUSINESS_IMAGES_MAX);
			}

			businessImages = uploadBusinessImages(savedBusiness.getId(), request.getBusinessImages());
			savedBusiness.setBusinessImages(businessImages);
		}
		
		businessRepository.save(savedBusiness);
		return ApiResponse.builder().message(AppConstants.BUSINESS_ADDED_SUCCESSFULLY).statusCode(HttpStatus.OK.value()).build();
	}

	@Override
	public BusinessResponse getBusinessById(String businessId) {
		return null;
	}

	@Override
	public BusinessResponse updateBusiness(String businessId, CreateBusinessRequest request,
			List<MultipartFile> images) {
		Business business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.BUSINESS_NOT_FOUND));

		// Update basic fields
		business.setBusinessName(request.getBusinessName());
		business.setAddress(request.getAddress());
		business.setLatitude(request.getLatitude());
		business.setLongitude(request.getLongitude());
		business.setPhoneNumber(request.getPhoneNumber());
		business.setWebsite(request.getWebsite());

		// Update SubCategory if changed
		if (!business.getSubCategory().getId().equals(request.getSubCategoryId())) {
			SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SUBCATEGORY_NOT_FOUND));
			business.setSubCategory(subCategory);
		}

		Business updatedBusiness = businessRepository.save(business);

		// Update business hours
		List<BusinessHours> businessHours = businessHoursService.updateBusinessHours(updatedBusiness.getId(),
				request.getBusinessHours());
		updatedBusiness.setBusinessHours(businessHours);

		// Handle image updates
		if (images != null && !images.isEmpty()) {
			if (images.size() > 10) {
				throw new BadRequestException(ValidationConstants.BUSINESS_IMAGES_MAX);
			}
			List<BusinessImage> businessImages = uploadBusinessImages(updatedBusiness.getId(), images);
			updatedBusiness.setBusinessImages(businessImages);
		}
		return null;
	}

	private List<BusinessImage> uploadBusinessImages(String businessId, List<MultipartFile> images) {
		List<BusinessImage> businessImages = new ArrayList<>();
		int displayOrder = 1;

		for (MultipartFile image : images) {
			String imageUrl = cloudinaryService.uploadFile(image,"business-img");

			BusinessImage businessImage = BusinessImage.builder()
					.business(Business.builder().id(businessId).build())
					.imageUrl(imageUrl)
					.displayOrder(displayOrder++)
					.build();

			businessImages.add(businessImage);
		}

		return businessImages;
	}

}
