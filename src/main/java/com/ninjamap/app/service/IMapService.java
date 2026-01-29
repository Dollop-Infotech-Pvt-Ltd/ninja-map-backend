package com.ninjamap.app.service;

import org.apache.http.impl.client.RoutedRequest;
import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.request.RouteRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IMapService {

	public ResponseEntity<?> search(String searchTerm, Integer size);
	public ResponseEntity<?> route(RouteRequest requestBody,String token);
	public ResponseEntity<?> reverse(double lat,double lon,String searchTerm,String token);
}
