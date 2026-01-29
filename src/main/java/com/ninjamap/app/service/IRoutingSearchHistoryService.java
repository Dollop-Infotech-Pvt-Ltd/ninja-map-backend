package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.RoutingSearchRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IRoutingSearchHistoryService {

	public ApiResponse createHistroy(RoutingSearchRequest routingSearchRequest);
	
	public ApiResponse getRoutingSearchHistory(Integer pageSize, Integer pageNumber, String sortDirection, String sortKey);
	
	public ApiResponse getRoutingSearchHistoryByUserId(String userId,PaginationRequest paginationRequest);
}
