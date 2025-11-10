package com.ninjamap.app.model;

import com.ninjamap.app.utils.constants.ValidationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PersonalInfo {

	@Column(name = "first_name", length = 100)
	@NotBlank(message = ValidationConstants.FIRST_NAME_REQUIRED)
	private String firstName;

	@Column(name = "last_name", length = 100)
	@NotBlank(message = ValidationConstants.LAST_NAME_REQUIRED)
	private String lastName;

	@Column(name = "email", unique = true, length = 150)
	@NotBlank(message = ValidationConstants.EMAIL_REQUIRED)
	@Email(regexp = ValidationConstants.EMAIL_PATTERN, message = ValidationConstants.EMAIL_PATTERN_MESSAGE)
	private String email;

	@Column(name = "mobile_number", length = 20)
	private String mobileNumber;

	@Column(name = "profile_picture", length = 500)
	private String profilePicture;

	@Column(name = "gender", length = 500)
	private String gender;

	@Column(name = "bio", length = 500)
	private String bio;

	@Column(name = "password", length = 255)
	private String password;

	public String getFullName() {
		String first = (this.firstName == null ? "" : this.firstName.trim());
		String last = (this.lastName == null ? "" : this.lastName.trim());
		String full = (first + " " + last).trim();
		return full.isEmpty() ? "User" : full;
	}
}
