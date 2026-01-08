package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "place")
public class Place extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;
	
	@Column
	private String emojiPic;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "category_id")
//	private Category category;
//
//	@Enumerated(EnumType.STRING)
//	@Column(nullable = false)
//	private PlaceType placeType;
//
//	public enum PlaceType {
//		CATEGORY, CUSTOM
//	}
}
