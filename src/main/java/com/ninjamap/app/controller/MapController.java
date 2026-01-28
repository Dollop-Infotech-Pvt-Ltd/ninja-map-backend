package com.ninjamap.app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IMapService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Validated
public class MapController {

	@Autowired
	private IMapService mapService;
	
	@GetMapping("/search")
	public ResponseEntity<?> search(
			@RequestParam(required = false) String search ,
			@RequestParam(required = true) Integer size
			){
		return this.mapService.search(search,size);
	}
	
	@PostMapping("/route")
	public ResponseEntity<?> routing(@RequestBody Object body){
		return this.mapService.route(body);
	}
	
	@GetMapping("/reverse-geocoding")
	public ResponseEntity<?> reverse(
			@RequestHeader(required = false) String token,
			@RequestParam(required = true) double lat ,
			@RequestParam(required = true) double lon ,
			@RequestParam(required = false) String searchTerm
			
			){
		return this.mapService.reverse(lat,lon,searchTerm,token);
	}
}
