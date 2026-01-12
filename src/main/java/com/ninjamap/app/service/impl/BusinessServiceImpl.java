package com.ninjamap.app.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Business;
import com.ninjamap.app.model.BusinessHours;
import com.ninjamap.app.model.BusinessImage;
import com.ninjamap.app.model.SubCategory;
import com.ninjamap.app.payload.request.CreateBusinessRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.BusinessHoursResponse;
import com.ninjamap.app.payload.response.BusinessImageResponse;
import com.ninjamap.app.payload.response.BusinessResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.SimpleSubCategoryResponse;
import com.ninjamap.app.repository.IBusinessRepository;
import com.ninjamap.app.repository.ISubCategoryRepository;
import com.ninjamap.app.service.IBusinessHoursService;
import com.ninjamap.app.service.IBusinessService;
import com.ninjamap.app.service.ICloudinaryService;
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
	public BusinessResponse createBusiness(CreateBusinessRequest request ) {
		
		System.err.println(request);
		
		// Check if phone number already exists
		if (businessRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
			throw new ResourceAlreadyExistException("Business with this phone number already exists");
		}

		// Validate and fetch SubCategory
		SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("SubCategory not found"));

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
		return mapToBusinessResponse(savedBusiness);
	}

	@Override
	public BusinessResponse getBusinessById(String businessId) {
		Business business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Business not found"));

		return mapToBusinessResponse(business);
	}

	@Override
	public BusinessResponse updateBusiness(String businessId, CreateBusinessRequest request,
			List<MultipartFile> images) {
		Business business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Business not found"));

		// Check if phone number is being changed and if new number already exists
		if (!business.getPhoneNumber().equals(request.getPhoneNumber())) {
			if (businessRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
				throw new ResourceAlreadyExistException("Business with this phone number already exists");
			}
		}

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
					.orElseThrow(() -> new ResourceNotFoundException("SubCategory not found"));
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

		return mapToBusinessResponse(updatedBusiness);
	}

	@Override
	public void deleteBusiness(String businessId) {
		Business business = businessRepository.findById(businessId)
				.orElseThrow(() -> new ResourceNotFoundException("Business not found"));

		business.setIsDeleted(true);
		businessRepository.save(business);
	}

	@Override
	public ApiResponse getAllBusinesses(int pageIndex, int pageSize) {
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		Page<Business> page = businessRepository.findAll(pageable);

		List<BusinessResponse> responses = page.getContent().stream()
				.map(this::mapToBusinessResponse)
				.collect(Collectors.toList());
		
		
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

	private BusinessResponse mapToBusinessResponse(Business business) {
		List<BusinessHoursResponse> hoursResponses = business.getBusinessHours().stream()
				.map(bh -> BusinessHoursResponse.builder()
						.id(bh.getId())
						.weekday(bh.getWeekday())
						.isOpen24Hours(bh.getIsOpen24Hours())
						.isClosed(bh.getIsClosed())
						.openingTime(bh.getOpeningTime() != null ? bh.getOpeningTime().format(TIME_FORMATTER) : null)
						.closingTime(bh.getClosingTime() != null ? bh.getClosingTime().format(TIME_FORMATTER) : null)
						.build())
				.collect(Collectors.toList());

		List<BusinessImageResponse> imageResponses = business.getBusinessImages().stream()
				.map(bi -> BusinessImageResponse.builder()
						.id(bi.getId())
						.imageUrl(bi.getImageUrl())
						.displayOrder(bi.getDisplayOrder())
						.build())
				.collect(Collectors.toList());

		SimpleSubCategoryResponse subCategoryResponse = SimpleSubCategoryResponse.builder()
				.id(business.getSubCategory().getId())
				.subCategoryName(business.getSubCategory().getSubCategoryName())
				.build();

		return BusinessResponse.builder()
				.id(business.getId())
				.businessName(business.getBusinessName())
				.subCategory(subCategoryResponse)
				.address(business.getAddress())
				.latitude(business.getLatitude())
				.longitude(business.getLongitude())
				.phoneNumber(business.getPhoneNumber())
				.website(business.getWebsite())
				.businessHours(hoursResponses)
				.businessImages(imageResponses)
				.createdDate(business.getCreatedDate())
				.updatedDate(business.getUpdatedDate())
				.isActive(business.getIsActive())
				.build();
	}
}
