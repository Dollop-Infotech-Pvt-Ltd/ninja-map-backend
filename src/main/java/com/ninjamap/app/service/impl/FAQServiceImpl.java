package com.ninjamap.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.FAQ;
import com.ninjamap.app.model.QuestionAnswer;
import com.ninjamap.app.payload.request.FAQRequest;
import com.ninjamap.app.payload.request.QuestionAnswerDTO;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.FAQResponse;
import com.ninjamap.app.repository.IFAQRepository;
import com.ninjamap.app.service.ICloudinaryService;
import com.ninjamap.app.service.IFAQService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements IFAQService {

	private final IFAQRepository ifaqRepository;
	private final ICloudinaryService cloudinaryService;

	@Override
	public ResponseEntity<ApiResponse> createFAQ(FAQRequest request) {
		// Upload the category image to Cloudinary if provided
		String uploadedImageUrl = null;
		if (request.getCategoryImageUrl() != null && !request.getCategoryImageUrl().isEmpty()) {
			uploadedImageUrl = cloudinaryService.uploadFile(request.getCategoryImageUrl(), "Ninja_Map");
		}

		// Build FAQ entity
		FAQ faq = FAQ.builder().category(request.getCategory()).categoryImageUrl(uploadedImageUrl) // use Cloudinary URL
				.questions(request.getQuestions().stream()
						.map(q -> QuestionAnswer.builder().question(q.getQuestion()).answer(q.getAnswer()).build())
						.collect(Collectors.toList()))
				.build();

		// Save FAQ
		FAQ saved = ifaqRepository.save(faq);
		ApiResponse response = (saved != null) ? AppUtils.buildCreatedResponse(AppConstants.FAQ_CREATED_SUCCESS)
				: AppUtils.buildFailureResponse(AppConstants.FAQ_NOT_CREATED);

		return new ResponseEntity<>(response, response.getHttp());

	}

	@Override
	public ResponseEntity<List<FAQResponse>> getAllFAQs() {
		// Fetch all FAQs from the repository
		List<FAQ> list = ifaqRepository.findAllByIsDeletedFalse();

		// Map entities to DTOs
		return ResponseEntity.ok(list.stream().map(this::mapToResponse).toList());
	}

	@Override
	public ResponseEntity<ApiResponse> getFAQById(String id) {
		FAQ faq = findById(id);

		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(AppConstants.DATA_FATCH_SUCCESSFULLY, mapToResponse(faq)));
	}

	@Override
	public ResponseEntity<ApiResponse> updateFAQ(String id, FAQRequest request) {
		FAQ faq = findById(id);

		// Upload the new category image to Cloudinary if provided
		String uploadedImageUrl = faq.getCategoryImageUrl(); // default to existing
		if (request.getCategoryImageUrl() != null && !request.getCategoryImageUrl().isEmpty()) {
			uploadedImageUrl = cloudinaryService.uploadFile(request.getCategoryImageUrl(), "Ninja_Map");
		}

		// Update FAQ fields
		faq.setCategory(request.getCategory());
		faq.setCategoryImageUrl(uploadedImageUrl);
		faq.setQuestions(request.getQuestions().stream()
				.map(q -> QuestionAnswer.builder().question(q.getQuestion()).answer(q.getAnswer()).build())
				.collect(Collectors.toList()));

		FAQ updated = ifaqRepository.save(faq);

		ApiResponse response = (updated != null) ? AppUtils.buildSuccessResponse(AppConstants.FAQ_UPDATED_SUCCESS)
				: AppUtils.buildFailureResponse(AppConstants.FAQ_NOT_UPDATED);

		return new ResponseEntity<>(response, response.getHttp());

	}

	private FAQ findById(String id) {
		return ifaqRepository.findByIdAndIsDeletedFalse(id)
				.orElseThrow(() -> new ResourceNotFoundException("FAQ not found"));

	}

	@Override
	public ResponseEntity<ApiResponse> deleteFAQ(String id) {
		FAQ faq = findById(id);

		faq.setIsActive(false);
		faq.setIsDeleted(true);
		ifaqRepository.save(faq);

		FAQ saved = ifaqRepository.save(faq);

		ApiResponse response = (saved != null) ? AppUtils.buildSuccessResponse(AppConstants.FAQ_DELETED_SUCCESS)
				: AppUtils.buildFailureResponse(AppConstants.FAQ_NOT_UPDATED);

		return new ResponseEntity<>(response, response.getHttp());

	}

	private FAQResponse mapToResponse(FAQ faq) {
		return FAQResponse.builder().id(faq.getId()).category(faq.getCategory())
				.categoryImageUrl(faq.getCategoryImageUrl())
				.questions(faq.getQuestions().stream()
						.map(q -> QuestionAnswerDTO.builder().question(q.getQuestion()).answer(q.getAnswer()).build())
						.collect(Collectors.toList()))
				.build();
	}
}
