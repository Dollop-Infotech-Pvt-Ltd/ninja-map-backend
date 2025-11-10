package com.ninjamap.app.enums;

public enum OtpType {
	LOGIN, REGISTER, FORGET_PASSWORD, RESET_PASSWORD, DELETE_ACCOUNT, MOBILE_VERIFICATION;

	/**
	 * Returns a user-friendly string: - lowercase - underscores replaced by spaces
	 */
//	public String toFriendlyString() {
//		return this.name().toLowerCase().replace("_", " ");
//	}

	/**
	 * Returns a user-friendly string: - lowercase - underscores replaced by spaces
	 * - each word's first letter capitalized
	 */
	public String toFriendlyString() {
		String formatted = this.name().toLowerCase().replace("_", " ");
		String[] words = formatted.split(" ");
		StringBuilder sb = new StringBuilder();

		for (String word : words) {
			if (!word.isEmpty()) {
				sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
			}
		}
		return sb.toString().trim();
	}
}
