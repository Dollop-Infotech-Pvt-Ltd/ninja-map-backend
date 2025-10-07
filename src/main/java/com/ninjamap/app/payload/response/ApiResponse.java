package com.ninjamap.app.payload.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class ApiResponse {
	private Boolean success;
	private String message;
	private HttpStatus http;
	private Object data;
	private Integer statusCode;

	public ApiResponse(Boolean success, String message, HttpStatus http) {
		super();
		this.success = success;
		this.message = message;
		this.http = http;
	}

}
