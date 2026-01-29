package com.ninjamap.app.payload.request;
import lombok.Data;

@Data
public class Location {
	   private Double lat;
	    private Double lon;

	    private String search_term;
	    private String full_name;

	    private Integer search_radius;
}
