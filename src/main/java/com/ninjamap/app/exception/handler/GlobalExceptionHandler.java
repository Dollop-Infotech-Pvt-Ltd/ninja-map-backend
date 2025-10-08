package com.ninjamap.app.exception.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.exception.FileValidationException;
import com.ninjamap.app.exception.ForbiddenException;
import com.ninjamap.app.exception.ResourceAlreadyExistException;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.payload.response.ErrorResponse;
import com.ninjamap.app.utils.constants.AppConstants;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleResourceAlreadyExist(ResourceAlreadyExistException ex) {
		return buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, AppConstants.TOKEN_EXPIRED);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex) {
		String message = ex.getParameterName() + " is required";
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
				.orElse(AppConstants.VALIDATION_FAILED);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<Map<String, String>> handleMissingPathVariable(MissingPathVariableException ex) {
		Map<String, String> response = new HashMap<>();
		response.put(AppConstants.ERROR, "Required path variable '" + ex.getVariableName() + "' is missing");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put(AppConstants.ERROR, AppConstants.METHOD_NOT_ALLOWED);
		response.put(AppConstants.MESSAGE, "Request method " + ex.getMethod() + " is not supported.");
		response.put(AppConstants.STATUS, HttpStatus.METHOD_NOT_ALLOWED.value());
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
		Map<String, String> response = new HashMap<>();
		response.put(AppConstants.ERROR, AppConstants.REQUEST_BODY_MISSING);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// Utility method to reduce duplication
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
		ErrorResponse errorResponse = ErrorResponse.builder().statusCode(status.value()).status(status).message(message)
				.date(LocalDateTime.now()).build();
		return ResponseEntity.status(status).body(errorResponse);
	}

	// Handle SQL constraint violations like duplicate entries
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		String message = "Database error occurred";

		// Optionally, check if it's a duplicate entry
		if (ex.getRootCause() != null && ex.getRootCause().getMessage().contains("Duplicate entry")) {
			message = "Duplicate entry found. Please use a different value.";
		}

		ErrorResponse errorResponse = ErrorResponse.builder().message(message).status(HttpStatus.BAD_REQUEST)
				.statusCode(HttpStatus.BAD_REQUEST.value()).date(LocalDateTime.now()).response(null).build();

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Handle validation errors like invalid UUID, etc.
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		// Combine all violation messages into a single string
		String message = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.collect(Collectors.joining("; ")); // joins all messages with a semicolon

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(HandlerMethodValidationException ex) {
		// Collect all validation error messages
		String errorMessages = ex.getAllValidationResults().stream().flatMap(r -> r.getResolvableErrors().stream())
				.map(err -> err.getDefaultMessage()).collect(Collectors.joining(", "));

		return buildErrorResponse(HttpStatus.BAD_REQUEST,
				errorMessages.isEmpty() ? "Validation failure" : errorMessages);
	}

	// Handle custom file validation exceptions
	@ExceptionHandler(FileValidationException.class)
	public ResponseEntity<ErrorResponse> handleFileValidationException(FileValidationException ex) {
//		ex.printStackTrace();
		return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage());
	}

	// Handle any other runtime exceptions
//	@ExceptionHandler(RuntimeException.class)

	// Handle Spring multipart max file size exceeded
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
		long maxSizeInMB = 10; // match your Spring configuration
		String message = "File size exceeds the maximum allowed limit of " + maxSizeInMB + " MB.";
		return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, message);
	}
}
