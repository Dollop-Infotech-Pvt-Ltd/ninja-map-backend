package com.ninjamap.app.model;

import java.util.ArrayList;
import java.util.List;

import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@Builder
@EqualsAndHashCode(callSuper = false)
public class Roles extends AuditData {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@UUIDValidator
	@Column(nullable = false, unique = true)
	private String roleId;

	@NotNull(message = ValidationConstants.ROLE_NAME_REQUIRED)
	@Column(nullable = false)
	private String roleName;

	@NotNull(message = ValidationConstants.DESCRIPTION_REQUIRED)
	@Column(nullable = false)
	private String description;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	@Builder.Default
	private List<Permission> permissions = new ArrayList<>();

}
