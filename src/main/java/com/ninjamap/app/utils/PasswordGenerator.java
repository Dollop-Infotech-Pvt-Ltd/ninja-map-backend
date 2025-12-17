package com.ninjamap.app.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL = "@$!%*?&";

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 30;

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generatePassword() {
		int length = RANDOM.nextInt((MAX_LENGTH - MIN_LENGTH) + 1) + MIN_LENGTH;
		return generatePassword(length);
	}

	public static String generatePassword(int length) {
		if (length < MIN_LENGTH || length > MAX_LENGTH) {
			throw new IllegalArgumentException("Password length must be between " + MIN_LENGTH + " and " + MAX_LENGTH);
		}

		List<Character> passwordChars = new ArrayList<>();

		// Ensure at least one of each required character type
		passwordChars.add(randomChar(UPPERCASE));
		passwordChars.add(randomChar(LOWERCASE));
		passwordChars.add(randomChar(DIGITS));
		passwordChars.add(randomChar(SPECIAL));

		// Fill the remaining characters
		String allAllowed = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
		for (int i = passwordChars.size(); i < length; i++) {
			passwordChars.add(randomChar(allAllowed));
		}

		// Shuffle for randomness
		Collections.shuffle(passwordChars, RANDOM);

		// Build final password
		StringBuilder password = new StringBuilder();
		passwordChars.forEach(password::append);

		return password.toString();
	}

	private static char randomChar(String source) {
		return source.charAt(RANDOM.nextInt(source.length()));
	}
}
