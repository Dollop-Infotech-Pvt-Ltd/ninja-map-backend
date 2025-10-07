package com.ninjamap.app.utils.annotations;

import java.util.List;
import java.util.UUID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UUIDValidatorService implements ConstraintValidator<UUIDValidator, Object> {

	private boolean required;

	@Override
	public void initialize(UUIDValidator constraintAnnotation) {
		this.required = constraintAnnotation.required();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return !required;
		}
		if (value instanceof String) {
			if (((String) value).isEmpty()) {
				return !required;
			}
			return this.isValidUUID((String) value);
		}
		if (value instanceof List<?>) {
			for (String uuid : (List<String>) value)
				if (!isValidUUID(uuid))
					return false;

		}
		return true;
	}

	public boolean isValidUUID(String id) {
		try {
			UUID.fromString(id);
			return true;
		} catch (Exception e) {
			System.err.println("UUID :: " + e.getMessage());
			return false;
		}
	}

}
