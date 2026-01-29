package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)
public class RoutingSearchHistory extends AuditData{

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false, length = 255)
	private String searchTerm;
	
	@Column(nullable = false, length = 255)
	private Double lat;
	
	@Column(nullable = false, length = 255)
	private Double lon;
 
	@Column(nullable = false, length = 255)
	private Integer searchRadius;
	
	@Column(nullable = false, length = 255)
	private String costing;
	
	@Builder.Default
	private Double useFerry=0.0;
	
    @Builder.Default
	private Integer ferryCost=300000;

}
