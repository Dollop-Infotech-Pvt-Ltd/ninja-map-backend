package com.ninjamap.app.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.model.Business;
import com.ninjamap.app.model.BusinessHours;
import com.ninjamap.app.payload.request.BusinessHoursRequest;
import com.ninjamap.app.repository.IBusinessHoursRepository;
import com.ninjamap.app.service.IBusinessHoursService;
import com.ninjamap.app.utils.constants.ValidationConstants;

@Service
public class BusinessHoursServiceImpl implements IBusinessHoursService {

	@Autowired
	private IBusinessHoursRepository businessHoursRepository;

	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	@Override
	public List<BusinessHours> createBusinessHours(String businessId, List<BusinessHoursRequest> requests) {
		List<BusinessHours> businessHoursList = new ArrayList<>();

		for (BusinessHoursRequest request : requests) {
			validateBusinessHoursRequest(request);
			BusinessHours businessHours = BusinessHours.builder()
					.business(Business.builder().id(businessId).build())
					.weekday(request.getWeekday())
					.isOpen24Hours(request.getIsOpen24Hours() != null ? request.getIsOpen24Hours() : false)
					.isClosed(request.getIsClosed() != null ? request.getIsClosed() : false)
					.build();

			if (!businessHours.getIsOpen24Hours() && !businessHours.getIsClosed()) {
				if (request.getOpeningTime() != null && request.getClosingTime() != null) {
					LocalTime openingTime = LocalTime.parse(request.getOpeningTime(), TIME_FORMATTER);
					LocalTime closingTime = LocalTime.parse(request.getClosingTime(), TIME_FORMATTER);

					if (openingTime.isAfter(closingTime) || openingTime.equals(closingTime)) {
						throw new BadRequestException(ValidationConstants.BUSINESS_HOURS_INVALID);
					}

					businessHours.setOpeningTime(openingTime);
					businessHours.setClosingTime(closingTime);
				}
			}

			businessHoursList.add(businessHoursRepository.save(businessHours));
		}

		return businessHoursList;
	}

	@Override
	public List<BusinessHours> updateBusinessHours(String businessId, List<BusinessHoursRequest> requests) {
		businessHoursRepository.deleteByBusinessId(businessId);
		return createBusinessHours(businessId, requests);
	}

	@Override
	public List<BusinessHours> getBusinessHours(String businessId) {
		return businessHoursRepository.findByBusinessId(businessId);
	}

	private void validateBusinessHoursRequest(BusinessHoursRequest request) {
		if (request.getWeekday() == null) {
			throw new BadRequestException("Weekday is required for business hours");
		}

		boolean isOpen24Hours = request.getIsOpen24Hours() != null && request.getIsOpen24Hours();
		boolean isClosed = request.getIsClosed() != null && request.getIsClosed();

		if (isOpen24Hours && isClosed) {
			throw new BadRequestException(ValidationConstants.BUSINESS_HOURS_INVALID);
		}

		if (!isOpen24Hours && !isClosed) {
			if (request.getOpeningTime() == null || request.getClosingTime() == null) {
				throw new BadRequestException(ValidationConstants.BUSINESS_HOURS_INVALID);
			}
		}
	}
}
