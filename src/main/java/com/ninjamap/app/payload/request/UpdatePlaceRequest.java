package com.ninjamap.app.payload.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePlaceRequest {
	
	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 500, message = "Name must be between 1 and 500 characters")
	private String name;

	
	private String placePic;
}
