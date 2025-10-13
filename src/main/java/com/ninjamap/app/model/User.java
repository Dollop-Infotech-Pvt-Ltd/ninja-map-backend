package com.ninjamap.app.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.constraints.UUID;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
//@EqualsAndHashCode(callSuper = false)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends AuditData {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@UUID
	@Column(nullable = false, unique = true)
	@EqualsAndHashCode.Include
	private String userId;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''", nullable = false)
	@NotBlank(message = ValidationConstants.FIRST_NAME_REQUIRED)
	private String firstName;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''", nullable = false)
	@NotBlank(message = ValidationConstants.LAST_NAME_REQUIRED)
	private String lastName;

	@Column(columnDefinition = "VARCHAR(100) DEFAULT ''", nullable = false)
	@NotBlank(message = ValidationConstants.EMAIL_REQUIRED)
	@Email(regexp = ValidationConstants.EMAIL_PATTERN, message = ValidationConstants.EMAIL_PATTERN_MESSAGE)
	private String email;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''")
	private String password;

	@Column(columnDefinition = "VARCHAR(50) DEFAULT ''")
	private String mobileNumber;

	@Column(columnDefinition = "VARCHAR(500) DEFAULT ''", nullable = true)
	private String profilePicture;

	@Column(columnDefinition = "VARCHAR(500) DEFAULT ''", nullable = true)
	private String bio;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Roles role;

	// Reverse mappings for engagement
	@ManyToMany(mappedBy = "likedByUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<BlogPost> likedPosts = new HashSet<>();

	@ManyToMany(mappedBy = "savedByUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<BlogPost> savedPosts = new HashSet<>();

	@ManyToMany(mappedBy = "sharedByUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<BlogPost> sharedPosts = new HashSet<>();

	@ManyToMany(mappedBy = "likedByUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<Comment> likedComments = new HashSet<>();

	@ManyToMany(mappedBy = "viewedByUsers", fetch = FetchType.LAZY)
	@Builder.Default
	private Set<BlogPost> viewedPosts = new HashSet<>();

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

}
