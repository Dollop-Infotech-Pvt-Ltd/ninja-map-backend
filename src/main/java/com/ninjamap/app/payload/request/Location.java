package com.ninjamap.app.payload.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
	   private Double lat;
	    private Double lon;

	    private String search_term;
	    private String full_name;

	    private Integer search_radius;
}
