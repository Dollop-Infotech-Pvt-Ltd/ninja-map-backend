package com.ninjamap.app.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<ApiResponse> search(
			@RequestParam(required = false) String q ,
			@RequestParam(required = true) String json
			){
		return ResponseEntity.ok(this.mapService.search());
	}
	
}
