package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.ninjamap.app.utils.constants.ValidationConstants;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonalInfo {

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

	@Column(columnDefinition = "VARCHAR(50) DEFAULT ''")
	private String mobileNumber;

	@Column(columnDefinition = "VARCHAR(500) DEFAULT ''", nullable = true)
	private String profilePicture;

	@Column(columnDefinition = "VARCHAR(500) DEFAULT ''", nullable = true)
	private String bio;

	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''")
	private String password;

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
}
