package com.ninjamap.app.utils.annotations;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class TrimValidatorService implements ConstraintValidator<TrimValidator, Object> {

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		if (obj == null)
			return true;

		for (Field field : obj.getClass().getDeclaredFields()) {
			if (field.getType().equals(String.class)) {
				field.setAccessible(true);
				try {
					Object value = field.get(obj);
					if (value != null) {
						String str = (String) value;
						// Remove leading/trailing spaces and normalize multiple inner spaces
						str = str.trim().replaceAll("\\s+", " ");
						field.set(obj, str);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
