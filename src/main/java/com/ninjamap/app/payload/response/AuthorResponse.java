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
	private String designation;
	private String profilePicture;
	private String bio;
}
