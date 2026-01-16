package com.ninjamap.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorResponse {
	private String name;
	private String email;
	private String designation;
	private String organisationName;
	private String profilePicture;
	private String bio;
}
