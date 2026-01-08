package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "search_history", indexes = {
	@Index(name = "idx_user_id", columnList = "user_id"),
	@Index(name = "idx_user_created_date", columnList = "user_id, created_date"),
	@Index(name = "idx_created_date", columnList = "created_date")
})
public class SearchHistory extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false, length = 255)
	private String searchTerm;
	
	

}
