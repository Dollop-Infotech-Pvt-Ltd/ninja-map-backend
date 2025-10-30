package com.ninjamap.app.model;

import com.ninjamap.app.enums.PermissionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permissions")
@Setter
@Getter
@ToString
@Builder
//@EqualsAndHashCode(of = "permissionId") // ensures uniqueness in sets
public class Permission extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(nullable = false, unique = true)
	private String permissionId;

	@Column(nullable = false)
	private String resource; // Module name, e.g., USER_MANAGEMENT, ROLE_MANAGEMENT

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PermissionType type; // READ, WRITE, DELETE, UPDATE

	@Column(nullable = false, unique = true)
	private String action; // Specific action, e.g., VIEW_USERS, CREATE_USERS

	// Helper factory method for convenience
	public static Permission of(String resource, PermissionType type, String action) {
		return Permission.builder().resource(resource).type(type).action(action).build();
	}
}
