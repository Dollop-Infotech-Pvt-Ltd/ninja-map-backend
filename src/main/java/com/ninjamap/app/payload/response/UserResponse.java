package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private String id;

	private String email;
	
	private String firstName;
	
	private String lastName;

	private String fullName;

	private String mobileNumber;

	private String profilePicture;

	private Boolean isActive;

	private String bio;
	
	private String gender;

	// Joining date field
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime joiningDate;

	private String role;

}