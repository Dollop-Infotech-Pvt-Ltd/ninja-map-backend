package com.ninjamap.app.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> route(Object requestBody) {

	    try {
	        String url = mapServiceUrlRoute + "/route";

	        // Incoming JSON as Map
	        Map<String, Object> body = (Map<String, Object>) requestBody;

	        // Build Valhalla locations[]
	        List<Map<String, Object>> locations = new ArrayList<>();

	        // from (required)
	        Map<String, Object> from = (Map<String, Object>) body.get("from");
	        if (from == null) {
	            return ResponseEntity
	                    .badRequest()
	                    .body("from location is required");
	        }
	        locations.add(from);

	        // via (optional)
	        List<Map<String, Object>> via = (List<Map<String, Object>>) body.get("via");
	        if (via != null && !via.isEmpty()) {
	            locations.addAll(via);
	        }

	        // to (required)
	        Map<String, Object> to = (Map<String, Object>) body.get("to");
	        if (to == null) {
	            return ResponseEntity
	                    .badRequest()
	                    .body("to location is required");
	        }
	        locations.add(to);

	        // Build final Valhalla body
	        Map<String, Object> valhallaBody = new HashMap<>();
	        valhallaBody.put("locations", locations);
	        
	        body.forEach((key, value) -> {
	            if (!"from".equals(key) && !"via".equals(key) && !"to".equals(key)) {
	                valhallaBody.put(key, value);
	            }
	        });
	        
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Object> entity = new HttpEntity<>(valhallaBody, headers);

	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                entity,
	                String.class
	        );

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
