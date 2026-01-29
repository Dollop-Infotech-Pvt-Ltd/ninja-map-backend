package com.ninjamap.app.service;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.response.ApiResponse;

public interface IMapService {

	public ResponseEntity<?> search(String searchTerm, Integer size);
	public ResponseEntity<?> route(Object requestBody,String token);
	public ResponseEntity<?> reverse(double lat,double lon,String searchTerm,String token);
}
