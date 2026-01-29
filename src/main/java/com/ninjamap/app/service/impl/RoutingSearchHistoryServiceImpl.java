package com.ninjamap.app.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ninjamap.app.model.RoutingSearchHistory;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.request.RoutingSearchRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.PaginatedResponse;
import com.ninjamap.app.payload.response.RoutingSearchHistoryResponse;
import com.ninjamap.app.payload.response.UserResponse;
import com.ninjamap.app.repository.IRoutingSearchHistoryRepository;
import com.ninjamap.app.service.IRoutingSearchHistoryService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.constants.AppConstants;

@Service
public class RoutingSearchHistoryServiceImpl implements IRoutingSearchHistoryService {

	@Autowired
	private IRoutingSearchHistoryRepository routingRepo;
	
	@Autowired
	private IUserService userService;
	
	@Override
	public ApiResponse createHistroy(RoutingSearchRequest routingSearchRequest) {
		UserResponse currectUserFromToken = this.userService.getCurrectUserFromToken();
	    this.routingRepo.save(this.convertToModel(routingSearchRequest, currectUserFromToken));
		return ApiResponse.builder().message(AppConstants.ROUTING_CREATED_SUCCESSFULLY).statusCode(HttpStatus.CREATED.value()).build();
	}
	
	private RoutingSearchHistory convertToModel(RoutingSearchRequest routingSearchRequest, UserResponse user) {
		return RoutingSearchHistory.builder()
			.costing(routingSearchRequest.getCosting())
			.ferryCost(routingSearchRequest.getFerryCost())
			.lat(routingSearchRequest.getLat())
			.userId(user.getId())
			.fullName(routingSearchRequest.getFullName())
			.lon(routingSearchRequest.getLon())
			.searchRadius(routingSearchRequest.getSearchRadius())
			.searchTerm(routingSearchRequest.getSearchTerm())
			.build();
	}

	@Override
	public ApiResponse getRoutingSearchHistory(Integer pageSize, Integer pageNumber, String sortDirection, String sortKey) {
		try {
			Sort.Direction direction = Sort.Direction.DESC;
			if (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) {
				direction = Sort.Direction.ASC;
			}
			
			String sortBy = (sortKey == null || sortKey.isEmpty() || sortKey.equalsIgnoreCase("null")) ? "createdAt" : sortKey;
			Sort sort = Sort.by(direction, sortBy);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
			
			Page<RoutingSearchHistory> searchHistory = this.routingRepo.findAll(pageable);
			
			return ApiResponse.builder()
					.success(true)
					.message(AppConstants.SEARCH_FETCHED_SUCCESSFULLY)
					.statusCode(HttpStatus.OK.value())
					.data(searchHistory)
					.build();
		} catch (Exception e) {
			return ApiResponse.builder()
					.success(false)
					.message(AppConstants.SOMETHING_WRONG)
					.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.build();
		}
	}

	@Override
	public ApiResponse getRoutingSearchHistoryByUserId(String userId,PaginationRequest paginationRequest) {
		
		Pageable pageable = PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize());
			Page<RoutingSearchHistory> searchHistory = this.routingRepo.findByUserId(userId, pageable);
			return ApiResponse.builder()
					.success(true)
					.message(AppConstants.SEARCH_FETCHED_SUCCESSFULLY)
					.statusCode(HttpStatus.OK.value())
					.data(new PaginatedResponse<>(searchHistory.map(this::convertToResponse)))
					.build();
	
	}
	private RoutingSearchHistoryResponse convertToResponse(RoutingSearchHistory routingSearchHistory) {
		return RoutingSearchHistoryResponse.builder().costing(routingSearchHistory.getCosting()).ferryCost(routingSearchHistory.getFerryCost())
				.fullName(routingSearchHistory.getFullName())
				.id(routingSearchHistory.getId()).lat(routingSearchHistory.getLat()).lon(routingSearchHistory.getLon()).searchRadius(routingSearchHistory.getSearchRadius())
				.searchTerm(routingSearchHistory.getSearchTerm()).useFerry(routingSearchHistory.getUseFerry()).userId(routingSearchHistory.getUserId()).build();
	}
}
