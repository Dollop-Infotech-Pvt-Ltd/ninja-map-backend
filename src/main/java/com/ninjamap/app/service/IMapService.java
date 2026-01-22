package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.response.ApiResponse;

public interface IMapService {

	public ApiResponse search();
	
	public ApiResponse route();
	
	
	public ResponseEntity<?> reverse(double lat,double lon,String searchTerm,String token);
}
