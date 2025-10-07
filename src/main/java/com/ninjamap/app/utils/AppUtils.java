package com.ninjamap.app.utils;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.exception.BadRequestException;
import com.ninjamap.app.payload.request.PaginationRequest;
import com.ninjamap.app.payload.response.ApiResponse;

@Component
public class AppUtils {

	private final ModelMapper modelMapper;

	public AppUtils(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	/**
	 * Builds Pageable object using PaginationRequest.
	 */
	public static Pageable buildPageableRequest(PaginationRequest pageRequest, Class<?> entityClass) {
		String sortKey = pageRequest.getSortKey();

		if (sortKey != null && !sortKey.isBlank()) {
			if (!validateSortKey(entityClass, sortKey)) {
				throw new IllegalArgumentException("Invalid sort key: " + sortKey);
			}
		} else {
			sortKey = "createdDate"; // default
		}

		Sort.Direction direction = Sort.Direction.DESC;
		if (pageRequest.getSortDirection() != null && !pageRequest.getSortDirection().isBlank()) {
			try {
				direction = Sort.Direction.valueOf(pageRequest.getSortDirection().toUpperCase());
			} catch (IllegalArgumentException e) {
				// fallback to default
			}
		}

		return PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), Sort.by(direction, sortKey));
	}

	/**
	 * Validates if the provided sortKey exists in the given class or its
	 * superclasses.
	 */
	public static boolean validateSortKey(Class<?> modelClass, String sortKey) {
		Class<?> currentClass = modelClass;
		while (currentClass != null) {
			boolean exists = Arrays.stream(currentClass.getDeclaredFields())
					.anyMatch(field -> field.getName().equals(sortKey));
			if (exists)
				return true;
			currentClass = currentClass.getSuperclass();
		}
		return false;
	}

	/**
	 * Generic method to convert any source object to a target class Can be used for
	 * DTO → Entity or Entity → DTO
	 *
	 * @param source      Source object
	 * @param targetClass Target class
	 * @param <S>         Source type
	 * @param <T>         Target type
	 * @return Converted object, or null if source is null
	 */
	public <S, T> T convertTo(S source, Class<T> targetClass) {
		if (source == null)
			return null;
		return modelMapper.map(source, targetClass);
	}

	/**
	 * Safely parse a string into any Enum type (case-insensitive)
	 *
	 * @param enumClass The Enum class
	 * @param value     The string value to parse
	 * @param <E>       Enum type
	 * @return Enum constant
	 * @throws BadRequestException if the value is invalid
	 */
	public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
		if (value == null)
			return null; // Optional: return null for null input
		try {
			return Enum.valueOf(enumClass, value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid " + enumClass.getSimpleName() + ": " + value);
		}
	}

	/**
	 * Checks if Kafka broker is available
	 */
//	public boolean isKafkaAvailable() {
//		try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
//			client.describeCluster().nodes().get();
//			return true;
//		} catch (Exception e) {
//			System.err.println("[DEBUG] Kafka is DOWN: " + e.getMessage());
//			return false;
//		}
//	}

	// Generic reusable method
	public static ApiResponse buildResponse(boolean success, String message, HttpStatus status, Object data) {
		return ApiResponse.builder().success(success).message(message).http(status).statusCode(status.value())
				.data(data).build();
	}

	// Overloads for common use cases
	public static ApiResponse buildSuccessResponse(String message) {
		return buildResponse(true, message, HttpStatus.OK, null);
	}

	public static ApiResponse buildSuccessResponse(String message, Object data) {
		return buildResponse(true, message, HttpStatus.OK, data);
	}

	public static ApiResponse buildCreatedResponse(String message, Object data) {
		return buildResponse(true, message, HttpStatus.CREATED, data);
	}

	public static ApiResponse buildCreatedResponse(String message) {
		return buildResponse(true, message, HttpStatus.CREATED, null);
	}

	public static ApiResponse buildFailureResponse(String message) {
		return buildResponse(false, message, HttpStatus.BAD_REQUEST, null);
	}

	public static ApiResponse buildFailureResponse(String message, HttpStatus status) {
		return buildResponse(false, message, status, null);
	}

	public static String formatOtpType(OtpType otpType) {
		String[] words = otpType.name().toLowerCase().split("_"); // split by underscore
		StringBuilder formatted = new StringBuilder();

		for (String word : words) {
			if (word.isEmpty())
				continue;
			// Capitalize first letter + rest lowercase
			formatted.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		}

		return formatted.toString().trim();
	}

}
