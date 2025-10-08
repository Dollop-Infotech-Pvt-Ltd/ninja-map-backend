package com.ninjamap.app.utils.constants;

public class ValidationConstants {

	// User Validation
	public static final String USERNAME_REQUIRED = "Username is required";
	public static final String FIRST_NAME_REQUIRED = "First Name is required";
	public static final String LAST_NAME_REQUIRED = "Last Name is required";
	public static final String FULL_NAME_REQUIRED = "Full Name is required";
	public static final String FIRST_NAME_LENGTH = "First name must be at most 50 characters";
	public static final String LAST_NAME_LENGTH = "Last name must be at most 50 characters";
	public static final String BIO_LENGTH = "Bio must be between 10 and 300 characters";

	// Email Validation
	public static final String EMAIL_REQUIRED = "Email is required";
	public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	public static final String EMAIL_PATTERN_MESSAGE = "Invalid email format";

	// Contact Number Validation
	public static final String MOBILE_NUMBER_REQUIRED = "Mobile number is required";
	public static final int MOBILE_NUMBER_LENGTH = 10;
	public static final String MOBILE_NUMBER_PATTERN = "^(?:\\+234|234|0)?[789][01]\\d{8}$"; // Matches only number
	public static final String MOBILE_NUMBER_PATTERN_MESSAGE = "Mobile number is invalid";

	// Password Validation
	public static final String PASSWORD_REQUIRED = "Password is required";
	public static final int PASSWORD_MIN_LENGTH = 8;
	public static final int PASSWORD_MAX_LENGTH = 30;
	public static final String PASSWORD_SIZE_MESSAGE = "Password must be between 8 and 30 characters long";
	public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
	public static final String PASSWORD_PATTERN_MESSAGE = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
	public static final String NEW_PASSWORD_REQUIRED = "New password is required";

	// Role Validation
	public static final String ROLE_REQUIRED = "Role is required";
	public static final String ROLE_NAME_REQUIRED = "Role name is required";
	public static final String ROLE_NAME_SIZE = "Role name must be between 5 and 200 characters";
	public static final String DESCRIPTION_SIZE = "Description Size must be between 3 and 50 characters";
	public static final String PERMISSION_IDS_REQUIRED = "At least one permission must be selected";

	public static final String ROLE_TYPE_REQUIRED = "Role type is required";
	public static final String ROLE_CREATOR_ID_REQUIRED = "Role creator ID is required";
	public static final String ROLE_ID_REQUIRED = "Role ID is required";
	public static final String PERMISSION_ID_REQUIRED = "Permission ID is required";
	public static final String RESOURCE_REQUIRED = "Resource title required";
	public static final String ACTION_REQUIRED = "Action is required";
	public static final String PATTERN_FOR_CAPITAL = "^[A-Z_]+$";
	public static final String PATTERN_FOR_CAPITAL_PATTERN_MESSAGE = "Resource must be uppercase and use underscores instead of spaces";
	public static final String DESCRIPTION_REQUIRED = "Description is required";

	// UUID Validation
	public static final String INVALID_UUID = "Invalid UUID format";
	public static final String ID_REQUIRED = "Id is Required";

	/* ===================== PAGINATION & SORTING ===================== */
	public static final String PAGE_SIZE_REQUIRED = "Page size is required";
	public static final String PAGE_INDEX_REQUIRED = "Page index is required";
	public static final String SORT_ORDER_PATTERN_MESSAGE = "Sort order must be either 'ASC' or 'DESC'";
	public static final String SORT_ORDER_PATTERN = "ASC|DESC|asc|desc";

	/* ===================== OTP ===================== */
	public static final String OTP_REQUIRED = "OTP is required";
	public static final String OTP_LENGTH_VALIDATION = "OTP must be a 6-digit number";

	/* ===================== POLICY ===================== */
	public static final String DOCUMENT_TYPE_REQUIRED = "Document type is required";
	public static final String POLICY_DOCUMENT_ID_REQUIRED = "Policy Document id is required";
	public static final String DOCUMENT_CONTENT_REQUIRED = "Document content must not be blank";
	public static final String TITLE_REQUIRED = "Title is required";
	public static final String IMAGE_REQUIRED = "Image is required";
	public static final String DOCUMENT_ALREADY_ACTIVE = "Document is already active";
	public static final String DOCUMENT_ALREADY_INACTIVE = "Document is already inactive";

	/* ===================== STATIC PAGES ===================== */
	public static final String TYPE_REQUIRED = "Static page type is required";
	public static final String CONTENT_REQUIRED = "Content must not be blank";

	/* ===================== ABOUT US ===================== */
	public static final String MODIFIED_BY_REQUIRED = "Modified by is required";

	/* ===================== CONTACT US ===================== */
	public static final String NAME_REQUIRED = "Name is required";
	public static final String NAME_LENGTH = "Name must be at most 50 characters";
	public static final String MESSAGE_REQUIRED = "Message is required";
	public static final String MESSAGE_LENGTH = "Message must be at most 500 characters";
	public static final String STATUS_NOT_NULL = "Status must not be null";
	public static final String UPDATED_BY_NOT_BLANK = "UpdatedBy must not be blank";

	public static final String EMPLOYEE_ID_REQUIRED = "Employee Id is required";
	public static final String PERMISSION_TYPE_REQUIRED = "Permission Type is required";
	public static final String ALLOWED_REQUIRED = "Allowed field is required";
}
