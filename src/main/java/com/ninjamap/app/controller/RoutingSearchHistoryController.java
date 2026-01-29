package com.ninjamap.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IRoutingSearchHistoryService;
import com.ninjamap.app.utils.constants.AppConstants;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search/routing")
@RequiredArgsConstructor
@Validated
public class RoutingSearchHistoryController {

	@Autowired
	private IRoutingSearchHistoryService routingSearchHistoryService;
	
	public ResponseEntity<ApiResponse> getSearchRouting(
			@RequestParam(name = AppConstants.PAGE_SIZE, defaultValue = "10") Integer pageSize,
			@RequestParam(name = AppConstants.PAGE_NUMBER, defaultValue = "0") Integer pageNumber,
			@RequestParam(name = AppConstants.SORT_DIRECTION, defaultValue = AppConstants.DESC, required = false) String sortDirection,
			@RequestParam(name = AppConstants.SORT_KEY, required = false) String sortKey
			){
		
	   return null;
	}
}
