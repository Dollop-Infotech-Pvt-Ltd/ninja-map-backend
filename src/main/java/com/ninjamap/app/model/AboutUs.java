package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "about_us")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AboutUs {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

}
