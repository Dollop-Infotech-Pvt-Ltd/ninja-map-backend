package com.ninjamap.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequest {
	@NotBlank(message="From location is required")
    private Location from;
	
    private List<Location> via;
    
    @NotBlank(message="To location is required")
    private Location to;
    
    @NotBlank(message="Costing is required")
    private String costing;
    
    @NotBlank(message="user ferry is required")
    private Double use_ferry;
    
    @NotBlank(message="ferry cost  is required")
    private Integer ferry_cost;
    
    private Integer alternates;

    @Builder.Default
    private Boolean isSaved=false;
}
