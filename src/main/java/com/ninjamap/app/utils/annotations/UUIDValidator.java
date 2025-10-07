package com.ninjamap.app.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UUIDValidatorService.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UUIDValidator {

	boolean required() default false;

	String message() default ValidationConstants.INVALID_UUID;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}