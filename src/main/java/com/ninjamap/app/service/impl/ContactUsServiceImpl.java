package com.ninjamap.app.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.ContactUs;
import com.ninjamap.app.payload.request.ContactUsRequest;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.ContactUsResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.repository.IContactUsRepository;
import com.ninjamap.app.service.IContactUsService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements IContactUsService {

	private final IContactUsRepository contactUsRepository;

	@Override
	public ResponseEntity<ApiResponse> saveContact(ContactUsRequest request) {
		ContactUs contactUs = ContactUs.builder().fullName(request.getFullName())
				.emailAddress(request.getEmailAddress()).inquiryType(request.getInquiryType())
				.subject(request.getSubject()).message(request.getMessage()).build();

		ContactUs saved = contactUsRepository.save(contactUs);

		ApiResponse response = (saved != null) ? AppUtils.buildCreatedResponse(AppConstants.CONTACT_SAVED_SUCCESS)
				: AppUtils.buildFailureResponse(AppConstants.CONTACT_NOT_SAVED);
		return new ResponseEntity<>(response, response.getHttp());
	}

	@Override
	public ResponseEntity<PaginatedResponse<ContactUsResponse>> getAllContacts(PaginationRequest paginationRequest) {
		// Build Pageable using your utility (handles sortKey validation & default)
		Pageable pageable = AppUtils.buildPageableRequest(paginationRequest, ContactUs.class);

		// Fetch paginated results from repository
		Page<ContactUs> page = contactUsRepository.findByIsDeletedFalseAndIsActiveTrue(pageable);

		// Map entities to DTOs
		Page<ContactUsResponse> mappedPage = page.map(this::mapToResponse);

		PaginatedResponse<ContactUsResponse> paginatedResponse = new PaginatedResponse<>(mappedPage);
		return ResponseEntity.ok(paginatedResponse);
	}

	@Override
	public ResponseEntity<ApiResponse> getContactById(String id) {
		ContactUs entity = findById(id);

		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(AppConstants.CONTACT_RETRIEVED_SUCCESS, mapToResponse(entity)));
	}

	private ContactUs findById(String id) {
		return contactUsRepository.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new RuntimeException(AppConstants.CONTACT_NOT_FOUND));
	}

	@Override
	public ResponseEntity<ApiResponse> deleteContact(String id) {
		ContactUs contact = findById(id);

		// Soft delete
		contact.setIsDeleted(true);
		contact.setIsActive(false);

		ContactUs saved = contactUsRepository.save(contact);

		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.CONTACT_DELETED_SUCCESS)
				: AppUtils.buildFailureResponse(AppConstants.CONTACT_NOT_DELETED);

		return new ResponseEntity<>(response, response.getHttp());
	}

	private ContactUsResponse mapToResponse(ContactUs entity) {
		return ContactUsResponse.builder().id(entity.getId()).fullName(entity.getFullName())
				.emailAddress(entity.getEmailAddress()).inquiryType(entity.getInquiryType())
				.subject(entity.getSubject()).message(entity.getMessage()).build();
	}
}
