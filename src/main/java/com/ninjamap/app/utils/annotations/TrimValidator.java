package com.ninjamap.app.utils.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrimValidatorService.class)
@Documented
public @interface TrimValidator {
	String message() default "Trimmed successfully";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
