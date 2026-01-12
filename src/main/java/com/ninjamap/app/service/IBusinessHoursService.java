package com.ninjamap.app.service;

import java.util.List;

import com.ninjamap.app.model.BusinessHours;
import com.ninjamap.app.payload.request.BusinessHoursRequest;

public interface IBusinessHoursService {

	List<BusinessHours> createBusinessHours(String businessId, List<BusinessHoursRequest> requests);

	List<BusinessHours> updateBusinessHours(String businessId, List<BusinessHoursRequest> requests);

	List<BusinessHours> getBusinessHours(String businessId);
}
