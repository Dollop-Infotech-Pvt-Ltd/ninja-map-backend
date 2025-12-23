package com.ninjamap.app.service;

import com.ninjamap.app.payload.response.ApiResponse;

public interface IMapService {

	public ApiResponse search();
	
	public ApiResponse route();
	
	
	public ApiResponse reverse(double lat,double lon,String searchTerm);
}
