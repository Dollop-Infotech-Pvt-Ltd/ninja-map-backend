package com.ninjamap.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.MapServiceResponse;
import com.ninjamap.app.service.IMapService;
import com.ninjamap.app.service.ISearchHistoryService;

@Service
public class MapServiceImpl implements IMapService {
	
	
    @Value("${map.service.url}")
    private String mapServiceUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ISearchHistoryService searchHistoryService;

	@Override
	public ApiResponse search() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse route() {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public ApiResponse reverse(double lat, double lon,String searchTerm,String token) {

	    try {
	        String url = String.format(
	                "%s/reverse.php?lat=%s&lon=%s&format=json",
	                mapServiceUrl,
	                lat,
	                lon
	        );

	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

	        // REQUIRED by Nominatim
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        ResponseEntity<MapServiceResponse> response =
	                restTemplate.exchange(
	                        url,
	                        HttpMethod.GET,
	                        entity,
	                        MapServiceResponse.class
	                );

	        // Record the reverse geocoding search in history
	        try {
	        	   if (token != null && !token.isBlank()
	                       && searchTerm != null && !searchTerm.isBlank()) {
	        		   
	   	            SearchHistoryRequest historyRequest = SearchHistoryRequest.builder()
		                    .searchTerm(searchTerm)
		                    .build();
		            searchHistoryService.recordSearch(historyRequest);
	        	   }
	        } catch (Exception e) {
	            System.err.println("Failed to record reverse geocoding search in history: " + e.getMessage());
	        }

	        return ApiResponse.builder()
	                .success(true)
	                .message("Reverse geocoding completed successfully")
	                .http(HttpStatus.OK)
	                .statusCode(HttpStatus.OK.value())
	                .data(response.getBody())
	                .build();

	    } catch (Exception e) {
	        return ApiResponse.builder()
	                .success(false)
	                .message(e.getMessage())
	                .http(HttpStatus.INTERNAL_SERVER_ERROR)
	                .statusCode(500)
	                .build();
	    }
	}
}
