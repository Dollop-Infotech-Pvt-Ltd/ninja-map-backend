package com.ninjamap.app.service.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.RoutingSearchHistory;
import com.ninjamap.app.payload.request.RoutingSearchRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.repository.IRoutingSearchHistoryRepository;
import com.ninjamap.app.service.IRoutingSearchHistoryService;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class RoutingSearchHistoryServiceImpl implements IRoutingSearchHistoryService {

	
	@Autowired
	private IRoutingSearchHistoryRepository routingRepo;
	
	@Override
	public ApiResponse createHistroy(RoutingSearchRequest routingSearchRequest) {
	    this.routingRepo.save(this.convertToModel(routingSearchRequest));
		return ApiResponse.builder().message(AppConstants.ROUTING_CREATED_SUCCESSFULLY).statusCode(HttpStatus.CREATED.value()).build();
	}
	
	private RoutingSearchHistory convertToModel(RoutingSearchRequest routingSearchRequest) {
	   return RoutingSearchHistory.builder().costing(routingSearchRequest.getCosting())
		.ferryCost(routingSearchRequest.getFerryCost()).lat(routingSearchRequest.getLat())
		.lon(routingSearchRequest.getLon()).searchRadius(routingSearchRequest.getSearchRadius())
		.searchTerm(routingSearchRequest.getSearchTerm()).build();
		
	}

}
