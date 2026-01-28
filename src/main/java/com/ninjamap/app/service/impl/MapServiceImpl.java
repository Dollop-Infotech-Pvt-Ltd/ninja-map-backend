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
import com.ninjamap.app.service.IMapService;
import com.ninjamap.app.service.ISearchHistoryService;

@Service
public class MapServiceImpl implements IMapService {
	
	
    @Value("${map.service.url.pelias}")
    private String mapServiceUrlPelias;
    
    @Value("${map.service.url.route}")
    private String mapServiceUrlRoute;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ISearchHistoryService searchHistoryService;

	@Override
	public ResponseEntity<?> search(String searchTerm, Integer size) {

	    try {
	        String url = String.format(
	                "%s/search-detailed/%s?size=%d",
	                mapServiceUrlPelias,
	                searchTerm,
	                size != null ? size : 10
	        );

	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

	        // Required by Pelias / Nominatim
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                entity,
	                String.class
	        );

	        // ✅ Return Pelias response exactly like curl
	        return response;

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());
	    }
	}
	@Override
	public ResponseEntity<?> route(Object requestBody) {

	    try {
	        String url = mapServiceUrlRoute + "/route";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

	        // Valhalla does NOT strictly require User-Agent, but safe to keep
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                entity,
	                String.class
	        );

	        // ✅ Return Valhalla response AS-IS
	        return response;

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());
	    }
	}
	@Override
	public ResponseEntity<?> reverse(double lat, double lon, String searchTerm, String token) {

	    try {
	        String url = String.format(
	        		"%s/reverse-detailed/%s/%s?size=1",
	        		mapServiceUrlPelias,
	                lat,
	                lon
	        );

	        HttpHeaders headers = new HttpHeaders();
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

	        // REQUIRED by Nominatim
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        ResponseEntity<String> response =
	                restTemplate.exchange(
	                        url,
	                        HttpMethod.GET,
	                        entity,
	                        String.class
	                );

	        // ✅ Record reverse geocoding search (unchanged)
	        if (token != null && !token.isBlank()
	                && searchTerm != null && !searchTerm.isBlank()) {
	            try {
	                SearchHistoryRequest historyRequest = SearchHistoryRequest.builder()
	                        .searchTerm(searchTerm)
	                        .build();
	                searchHistoryService.recordSearch(historyRequest);
	            } catch (Exception e) {
	                System.err.println(
	                        "Failed to record reverse geocoding search in history: " + e.getMessage()
	                );
	            }
	        }

	        // ✅ Return API response AS-IS (same as curl)
	        System.out.println(response);
	        return response;

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());
	    }
	}

}
