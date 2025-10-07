package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDocumentResponse {
	private String id;
	private String title;
	private String description;
	private String image;
	private DocumentType documentType;

}
