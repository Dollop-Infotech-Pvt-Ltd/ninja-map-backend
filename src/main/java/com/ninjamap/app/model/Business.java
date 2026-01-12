package com.ninjamap.app.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "business")
public class Business extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false, length = 255)
	private String businessName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id", nullable = false)
	private SubCategory subCategory;

	@Column(nullable = false, length = 500)
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false, length = 20)
	private String phoneNumber;

	@Column(length = 255)
	private String website;

	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<BusinessHours> businessHours = new ArrayList<>();

	@OneToMany(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<BusinessImage> businessImages = new ArrayList<>();
}
