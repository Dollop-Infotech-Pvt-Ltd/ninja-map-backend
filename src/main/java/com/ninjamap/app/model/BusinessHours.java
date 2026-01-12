package com.ninjamap.app.model;

import java.time.LocalTime;

import com.ninjamap.app.enums.Weekday;

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
@Table(name = "business_hours")
public class BusinessHours extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_id", nullable = false)
	private Business business;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Weekday weekday;

	@Column(nullable = false)
	private Boolean isOpen24Hours = false;

	@Column(nullable = false)
	private Boolean isClosed = false;

	@Column
	private LocalTime openingTime;

	@Column
	private LocalTime closingTime;
}
