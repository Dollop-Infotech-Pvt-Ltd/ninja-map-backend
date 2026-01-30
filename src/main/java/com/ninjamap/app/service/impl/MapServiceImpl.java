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

import com.ninjamap.app.payload.request.Location;
import com.ninjamap.app.payload.request.RouteRequest;
import com.ninjamap.app.payload.request.RoutingSearchRequest;
import com.ninjamap.app.payload.request.SearchHistoryRequest;
import com.ninjamap.app.service.IMapService;
import com.ninjamap.app.service.IRoutingSearchHistoryService;
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
    private IRoutingSearchHistoryService routingSearchHistoryService;
    
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
	public ResponseEntity<?> route(RouteRequest requestBody, String token) {

	    try {
	        String url = mapServiceUrlRoute + "/route";

	        // ---------- Validate required fields ----------
	        if (requestBody.getFrom() == null) {
	            return ResponseEntity
	                    .badRequest()
	                    .body("from location is required");
	        }

	        if (requestBody.getTo() == null) {
	            return ResponseEntity
	                    .badRequest()
	                    .body("to location is required");
	        }

	        // ---------- Build Valhalla locations ----------
	        List<Map<String, Object>> locations = new ArrayList<>();

	        locations.add(convertLocation(requestBody.getFrom()));

	        if (requestBody.getVia() != null && !requestBody.getVia().isEmpty()) {
	            requestBody.getVia()
	                    .forEach(v -> locations.add(convertLocation(v)));
	        }

	        locations.add(convertLocation(requestBody.getTo()));

	        // ---------- Save routing history (only if allowed) ----------
	        if (token != null
	                && !token.isBlank()
	                && Boolean.TRUE.equals(requestBody.getIsSaved())) {
                   Location to = requestBody.getTo();

	            RoutingSearchRequest historyRequest =
	                    RoutingSearchRequest.builder()
	                            .lat(to.getLat())
	                            .lon(to.getLon())
	                            .fullName(to.getFull_name())
	                            .searchTerm(to.getSearch_term())
	                            .searchRadius(
	                                    to.getSearch_radius() == null
	                                            ? 500
	                                            : to.getSearch_radius()
	                            )
	                            .costing(
	                                    requestBody.getCosting() == null
	                                            ? "auto"
	                                            : requestBody.getCosting()
	                            )
	                            .useFerry(
	                                    requestBody.getUse_ferry() == null
	                                            ? 0.0
	                                            : requestBody.getUse_ferry()
	                            )
	                            .ferryCost(
	                                    requestBody.getFerry_cost() == null
	                                            ? 0
	                                            : requestBody.getFerry_cost()
	                            )
	                            .build();

	            routingSearchHistoryService.createHistroy(historyRequest);
	        }

	        // ---------- Build Valhalla request body ----------
	        Map<String, Object> valhallaBody = new HashMap<>();
	        valhallaBody.put("locations", locations);
	        valhallaBody.put("alternates", requestBody.getAlternates());
	        valhallaBody.put("costing", requestBody.getCosting());
	        valhallaBody.put("use_ferry", requestBody.getUse_ferry());
	        valhallaBody.put("ferry_cost", requestBody.getFerry_cost());

	        // ---------- Call Valhalla ----------
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
	        headers.set("User-Agent", "Open-Network-App/1.0 (contact: dev@yourdomain.com)");

	        HttpEntity<Object> entity = new HttpEntity<>(valhallaBody, headers);

	        return restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                entity,
	                String.class
	        );

	    } catch (Exception e) {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());
	    }
	}

	
	private Map<String, Object> convertLocation(Location location) {
	    Map<String, Object> map = new HashMap<>();
	    map.put("lat", location.getLat());
	    map.put("lon", location.getLon());
	    map.put("search_term", location.getSearch_term());
	    map.put("full_name", location.getFull_name());
	    map.put("search_radius", location.getSearch_radius());
	    return map;
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
