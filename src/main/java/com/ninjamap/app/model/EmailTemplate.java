package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_templates")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailTemplate {

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "template_id", nullable = false)
	private String templateId;

	@Column(unique = true)
	private String templateName;

	@Column(columnDefinition = "TEXT")
	private String body;
}