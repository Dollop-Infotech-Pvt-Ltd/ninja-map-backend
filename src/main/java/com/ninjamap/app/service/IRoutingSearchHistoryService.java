package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.RoutingSearchRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IRoutingSearchHistoryService {

	public ApiResponse createHistroy(RoutingSearchRequest routingSearchRequest);
	
	public ApiResponse getRoutingSearchHistory();
}
