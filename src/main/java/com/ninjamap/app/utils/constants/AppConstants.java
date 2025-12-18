package com.ninjamap.app.utils.constants;

public class AppConstants {
	// ================== PERMISSION ==================
	public static final String PERMISSION_NOT_FOUND = "Permission not found.";
	public static final String SUB_PERMISSION_NOT_FOUND = "Sub Permission not found.";
	public static final String PERMISSION_UPDATE_SUCCESSFULLY = "Permission updated successfully.";
	public static final String PERMISSION_UPDATE_FAILED = "Permission updated successfully."; // Duplicate message?
																								// Consider fixing
	public static final String PERMISSION_FETCHED_SUCCESSFULLY = "Permissions fetched successfully.";
	public static final String PERMISSION_REMOVED_SUCCESSFULLY = "Permission removed successfully";
	public static final String SUB_PERMISSION_REMOVED_SUCCESS = "Sub Permission removed successfully";
	public static final String PERMISSION_DELETE_SUCCESS = "Permission deleted successfully";
	public static final String PERMISSION_DELETE_FAILED = "Failed to delete permission";

	// ================== EMAIL ==================
	public static final String EMAIL_ALREADY_REGISTERED = "Email already registered.";
	public static final String MOBILE_ALREADY_REGISTERED = "Mobile already registered.";
	public static final String FROM_EMAIL = "no-reply@yourdomain.com";

	// ================== ROLE ==================
	public static final String ROLE_NOT_FOUND = "Selected role does not exist.";
	public static final String ADMIN_ROLE_NOT_FOUND = "Admin role not found.";
	public static final String INVALID_USER_ROLE = "Invalid user role.";
	public static final String ROLE_ALREADY_EXISTS = "Role already exists";
	public static final String ROLE_PERMISSION_UPDATED = "Role permissions updated successfully";
	public static final String ROLE_UPDATED = "Role updated successfully";
	public static final String ROLE_CREATED = "Role Successfully Created";
	public static final String ROLE_NOT_CREATED = "Role could not be created";
	public static final String PERMISSIONS_UPDATED_SUCCESS = "Permissions updated successfully for role: ";
	public static final String PERMISSIONS_NOT_UPDATED = "Permissions could not be updated for role: ";
	public static final String PERMISSION_CREATED = "Permission successfully Created";
	public static final String SUB_PERMISSION_ADDED_SUCCESS = "Sub Permission added successfully";
	public static final String PERMISSION_NOT_ADDED = "Permission could not be added";
	public static final String SUB_PERMISSION_NOT_ADDED = "Sub Permission could not be added";
	public static final String ROLE_DELETE_SUCCESS = "Role deleted successfully";
	public static final String ROLE_DELETE_FAILED = "Failed to delete Role";
	public static final String PERMISSION_ALREADY_EXISTS = "Permission already exists";

	// ================== COMMON KEYS ==================
	public static final String ERROR = "error";
	public static final String MESSAGE = "message";
	public static final String STATUS = "status";
	public static final String STATUS_CODE = "statusCode";
	public static final String DATE = "date";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String EVENT_TYPE = "eventType";
	public static final String PAYLOAD = "payload";
	public static final String ID = "id";
	public static final String IS_LIKE = "isLike";
	public static final String IS_SAVE = "isSave";
	public static final String SESSION_ID = "sessionId";
	public static final String CURRENT_SESSION_ID = "currentSessionId";
	public static final String KEEP_ONLY_CURRENT_SESSION = "keepOnlyCurrentSession";
	public static final String PERMISSION_ID = "permissionId";
	public static final String PERMISSION_TITLE = "permissionTitle";
	public static final String PERMISSION_TYPE = "permissionType";
	public static final String SUB_PERMISSION_ID = "subPermissionId";
	public static final String ROLE_ID = "roleId";
	public static final String BLOG_POST_ID = "blogPostId";
	public static final String COMMENT_ID = "commentId";
	public static final String REPLY_ID = "replyId";
	public static final String IS_ACTIVE = "isActive";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String SUBMITTED_AT = "submittedAt";
	public static final String ADMIN_PASSWORD = "Admin@123";
	public static final String ADMIN_EMAIL = "admin@gmail.com";
	public static final String ROLE = "role";
	public static final String TOKEN_TYPE = "tokenType";
	public static final String IS_OTP_VERIFIED = "isOtpVerified";
	public static final String OTP_TYPE = "otpType";
	public static final String AUTH_TOKEN = "authToken";
	public static final String OTP = "otp";
	public static final String USER_AGENT = "User-Agent";
	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REFRESH_TOKEN = "refreshToken";
	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String APPLICATION_JSON = "application/json";
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	public static final String COMPANY_NAME = "Ninja Map";
	public static final String COMPANY_EMAIL = "ninjamap@gmail.com";
	public static final String COMPANY_SUPPORT_LINK = "mailto:" + COMPANY_EMAIL;
	public static final String BLOG_THUMBNAILS = "Blog_Thumbnails";
	public static final String BLOG_POSTS = "Blog_Posts";
	public static final String FAQ_FOLDER = "FAQ_Folder";
	public static final String POLICY_DOCUMENTS = "Policy_Documents";
	public static final String PROFILE_PICTURE = "Profile_Picture";

	// ================== PAGINATION / SORT ==================
	public static final String PAGE_SIZE = "pageSize";
	public static final String PAGE_NUMBER = "pageNumber";
	public static final String SORT_DIRECTION = "sortDirection";
	public static final String SORT_KEY = "sortKey";
	public static final String SEARCH_VALUE = "searchValue";
	public static final String DESC = "DESC";

	// ================== OTP ==================
	public static final String OTP_SENT_FOR_LOGIN = "OTP sent to your registered email. Please verify.";
	public static final String OTP_SENT_FOR_RESET_PASSWORD = "OTP sent to your registered email for password reset.";
	public static final String OTP_SENT = "OTP sent. Please verify to continue.";
	public static final String OTP_ALREADY_VERIFIED = "OTP already verified. Cannot resend.";
	public static final String OTP_NOT_VERIFIED = "OTP is not verified.";
	public static final String INVALID_OR_EXPIRED_OTP = "Invalid or expired OTP.";
	public static final String RESEND_OTP_NOT_ALLOWED_FOR_THIS_TYPE = "OTP resend not allowed for this OTP type.";
	public static final String OTP_VERIFIED_SUCCESSFULLY = "OTP verified successfully.";
	public static final String MOBILE_NOT_REGISTERED = "Mobile number verified. Please complete your registration.";

	// ================== ADMIN ==================
	public static final String ADMIN_NOT_FOUND = "Admin not found.";
	public static final String ADMIN_CREATED_SUCCESSFULLY = "Admin created successfully.";
	public static final String ADMIN_NOT_CREATED = "Admin could not be created";
	public static final String INVALID_CREDENTIALS = "Invalid credentials.";
	public static final String ADMIN_DELETED = "Admin deleted successfully.";
	public static final String ADMIN_PROFILE_UPDATED = "Admin Profile Successfully Updated";
	public static final String ADMIN_PROFILE_NOT_UPDATED = "Admin profile could not be updated.";
	public static final String ADMIN_SUCCESSFULLY_FATCH = "Admin successfully get";
	public static final String ADMIN_ALREDY_DELETED = "Admin is already deleted";
	public static final String ADMIN_STATUS_UPDATED = "Admin status updated successfully";
	public static final String ADMIN_STATUS_NOT_UPDATED = "Admin status has not been updated";
	public static final String ADMIN_ALREADY_DELETED = "Admin is already deleted";
	public static final String ADMIN_ALREADY_ACTIVE = "Admin status is already ACTIVE";
	public static final String ADMIN_ALREADY_INACTIVE = "Admin status is already INACTIVE";
	public static final String ADMIN_ALREADY_EXISTS = "Admin already exists";
	public static final String ADMIN_REACTIVATED = "Deleted admin account reactivated successfully.";
	public static final String EMPLOYEE_ID_ALREADY_REGISTERED = "Employee ID already exists";
	public static final String ADMIN_ACCOUNT_INACTIVE = "Admin account is inactive. Please contact super-admin.";
	public static final String ADMIN_ACCOUNT_DELETED = "Admin account not found or deleted.";

	// ================== TOKEN ==================
	public static final String MISSING_TOKEN = "Missing token.";
	public static final String TOKEN_EXPIRED = "Token expired.";
	public static final String INVALID_TOKEN_TYPE_FOR_VERIFICATION = "Invalid token type or OTP type for verification.";
	public static final String INVALID_TOKEN_TYPE = "Invalid token type.";
	public static final String TOKEN_NOT_BELONG_TO_USER = "Token does not belong to this user.";
	public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found.";
	public static final String INVALID_TOKEN = "Invalid token.";
	public static final String REFRESH_TOKEN_NOT_RECOGNIZED = "Refresh token is invalid, expired, or revoked.";
	public static final String ACCESS_TOKEN_GENERATED = "Access token generated.";
	public static final String ACCESS_TOKEN_FETCH = "Access Token fetched successfully";

	// ================== SESSION ==================
	public static final String SESSION_NOT_FOUND = "Session not found.";
	public static final String SESSION_ALREADY_LOGGED_OUT = "Session is already logged out.";
	public static final String ALL_SESSION_LOGGED_OUT = "All other sessions logged out successfully.";
	public static final String CURRENT_SESSION_LOGGED_OUT = "Current session logged out successfully.";

	// ================== LOGOUT ==================
	public static final String LOGOUT = "Logout successful";

	// ================== PASSWORD ==================
	public static final String PASSWORD_SHOULD_BE_DIFFERENT = "New password must be different from the old password.";
	public static final String PASSWORD_RESET_SUCCESS = "Your password has been updated successfully.";

	// ================== ERRORS ==================
	public static final String REQUEST_BODY_MISSING = "Required request body is missing.";
	public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
	public static final String VALIDATION_FAILED = "Validation failed.";
	public static final String ACCESS_DENIED = "Access denied.";
	public static final String SERVICE_DOWN = "Downstream service is unavailable. Please try again later.";
	public static final String SERVICE_UNAVAILABLE = "Service is temporarily unavailable.";
	public static final String REQUEST_TIME_OUT = "Request timed out. Please try again.";
	public static final String SOMETHING_WRONG = "Something went wrong.";
	public static final String READ_TIME_OUT = "Read timed out.";
	public static final String UNAUTHORIZED = "Unauthorized.";
	public static final String FAILED_TO_SEND_KAFKA_MESSAGE = "Failed to send Kafka message";
	public static final String UNAUTHORIZED_ACCESS = "Unauthorized Access";
	public static final String REFRESH_TOKEN_NOT_ALLOWED = "Refresh token not allowed.";

	// ================== POLICY ==================
	public static final String POLICY_ALREADY_EXISTS = "Policy document with version '%s' already exists.";
	public static final String POLICY_NOT_FOUND = "Policy document not found.";
	public static final String POLICY_UPDATE_ONLY_ACTIVE = "Only active policy documents can be updated.";
	public static final String POLICY_DOCUMENT_NOT_FOUND = "Policy document not found.";
	public static final String POLICY_DOCUMENT_DELETED_SUCCESSFULLY = "Policy document deleted successfully.";
	public static final String POLICY_CREATED_SUCCESS = "Policy document created successfully.";
	public static final String POLICY_UPDATED_SUCCESS = "Policy document updated successfully.";
	public static final String DOCUMENT_STATUS_UPDATED_SUCCESSFULLY = "Document status updated successfully";
	public static final String DATA_FATCH_SUCCESSFULLY = "Data retrieved successfully";
	public static final String POLICY_NOT_CREATED = "Policy Document could not be created";
	public static final String POLICY_NOT_UPDATED = "Policy Document update failed";

	// ================== CONTACT US ==================
	public static final String CONTACT_US_SUBMITTED_SUCCESSFULLY = "Your message has been submitted successfully.";
	public static final String CONTACT_STATUS_UPDATED_SUCCESSFULLY = "Contact status updated successfully.";
	public static final String IGNORED_CANNOT_CHANGE_TO_RESOLVED_OR_SUBMITTED = "Cannot change IGNORED status to RESOLVED or SUBMITTED";
	public static final String OPEN_CANNOT_CHANGE_TO_SUBMITTED = "Cannot change OPEN status back to SUBMITTED";
	public static final String SUBMITTED_CANNOT_CHANGE_DIRECTLY_TO_RESOLVED_OR_IGNORED = "Cannot move SUBMITTED directly to RESOLVED or IGNORED. Must be OPEN first.";
	public static final String STATUS_ALREADY_SAME = "Status is already ";
	public static final String IGNORED_CANNOT_CHANGE_TO_RESOLVED_OR_SUBMITTED_OR_OPEN = "Cannot change IGNORED status to RESOLVED, SUBMITTED, or OPEN";
	public static final String RESOLVED_CANNOT_CHANGE_TO_SUBMITTED_OR_OPEN_OR_IGNORED = "Cannot change RESOLVED status back to SUBMITTED or OPEN or IGNORED";

	public static final String CONTACT_SAVED_SUCCESS = "Contact saved successfully";
	public static final String CONTACT_NOT_SAVED = "Contact not saved";
	public static final String CONTACT_UPDATED_SUCCESS = "Contact updated successfully";
	public static final String CONTACT_NOT_UPDATED = "Contact not updated";
	public static final String CONTACT_NOT_FOUND = "Contact not found";
	public static final String CONTACT_RETRIEVED_SUCCESS = "Contact retrieved successfully";
	public static final String CONTACT_DELETED_SUCCESS = "Contact deleted successfully";
	public static final String CONTACT_ALREADY_DELETED = "Contact is already deleted";
	public static final String CONTACT_NOT_DELETED = "Contact could not be deleted";

	// ================== ABOUT US ==================
	public static final String NO_CONTANT_AVAILABLE = "No content available.";
	public static final String ABOUT_US_UPDATED = "About us successfully updated";
	public static final String ABOUT_US_NOT_UPDATED = "About Us could not be updated";
	public static final String ABOUT_US_SUCCESSFULLY_GET = "About Us retrieved successfully";
	public static final String ABOUT_US_ADDED_SUCCESSFULLY = "About Us added successfully";
	public static final String ABOUT_US_NOT_ADDED = "About Us could not be added";

	// ================== USER ==================
	public static final String USER_SUCCESSFULLY_FATCH = "User successfully get";
	public static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
	public static final String USER_NOT_CREATED = "User could not be created";
	public static final String USER_NOT_FOUND = "User not found.";
	public static final String AUTHENTICATION_FAILED = "Authentication failed: ";
	public static final String USER_SUCCESSFULLY_GET = "User fetched successfully";
	public static final String USER_ALREADY_ACTIVE = "User status is already ACTIVE";
	public static final String USER_ALREADY_INACTIVE = "User status is already INACTIVE";
	public static final String USER_PROFILE_SUCCESSFULLY_UPDATED = "User profile updated successfully";
	public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
	public static final String USER_NOT_DELETED = "User could not be deleted";
	public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully created";
	public static final String USER_ALREDY_DELETED = "User is already deleted";
	public static final String USER_ALREADY_EXISTS = "User already exists";
	public static final String USER_PROFILE_NOT_UPDATED = "User profile could not be updated";
	public static final String USER_STATUS_UPDATED_SUCCESSFULLY = "User status updated successfully";
	public static final String USER_STATUS_NOT_UPDATED = "User status could not be updated";
	public static final String UNAUTHORIZED_DELETE = "You are not authorized to delete this account";
	public static final String USER_ACCOUNT_DELETED_SUCCESSFULLY = "Your account deleted successfully";
	public static final String USER_ACCOUNT_INACTIVE = "Your account is inactive. Please contact the administrator.";
	public static final String USER_ACCOUNT_DELETED = "User account not found or deleted.";
	public static final String USER_NOT_VERIFIED_OR_MISMATCH = "Mobile number not verified or does not match the verification record.";

	// ================== BLOG_POST ==================
	public static final String BLOG_POST_NOT_FOUND = "Blog post not found";
	public static final String BLOG_POST_CREATED = "Blog post created successfully";
	public static final String BLOG_POST_NOT_CREATED = "Blog post could not be created";
	public static final String BLOG_POST_DELETED = "Blog post deleted successfully";
	public static final String BLOG_POST_NOT_DELETED = "Blog post could not be deleted";
	public static final String BLOG_POST_ALREADY_DELETED = "Blog post is already deleted";
	public static final String BLOG_POST_UPDATED = "Blog post updated successfully";
	public static final String BLOG_POST_NOT_UPDATED = "Blog post could not be updated";
	public static final String BLOG_POST_FETCHED = "Blog post fetched successfully";
	public static final String BLOG_POST_LIKED = "Post liked successfully.";
	public static final String BLOG_POST_UNLIKED = "Post unliked successfully.";
	public static final String HOMEPAGE_POSTS_FETCHED = "Homepage posts fetched successfully.";
	public static final String BLOG_POST_SAVED = "Blog post saved successfully.";
	public static final String BLOG_POST_UNSAVED = "Blog post unsaved successfully.";
	public static final String BLOG_POST_SHARED = "Blog post shared successfully.";
	public static final String BLOG_POST_VIEWED = "Blog post view registered.";
	public static final String BLOG_POST_UNAUTHORIZED_UPDATE = "You are not authorized to update this blog post.";
	public static final String BLOG_POST_UNAUTHORIZED_DELETE = "You are not authorized to delete this blog post.";

	// ================== COMMENT ==================
	public static final String COMMENT_ADDED = "Comment added successfully.";
	public static final String COMMENT_NOT_ADDED = "Failed to add comment.";
	public static final String COMMENT_DELETED = "Comment deleted successfully.";
	public static final String COMMENT_NOT_DELETED = "Failed to delete comment.";
	public static final String COMMENT_NOT_FOUND = "Comment not found.";
	public static final String COMMENT_ALREADY_DELETED = "Comment is already deleted";
	public static final String COMMENT_LIKED = "Comment liked successfully.";
	public static final String COMMENT_UNLIKED = "Comment unliked successfully.";
	public static final String COMMENT_DELETE_FORBIDDEN = "You are not allowed to delete this comment.";

	public static final String REPLY_NOT_FOUND = "This comment is not a reply.";
	public static final String REPLY_DELETED = "Reply deleted successfully.";
	public static final String REPLY_DELETE_FORBIDDEN = "You are not allowed to delete this reply.";

	// ================== FAQ ==================
	public static final String FAQ_CREATED_SUCCESS = "FAQ created successfully";
	public static final String FAQ_NOT_CREATED = "FAQ could not be created";
	public static final String FAQ_UPDATED_SUCCESS = "FAQ updated successfully";
	public static final String FAQ_NOT_UPDATED = "FAQ could not be updated";
	public static final String FAQ_DELETED_SUCCESS = "FAQ deleted successfully";
	public static final String FAQ_NOT_FOUND = "FAQ not found with id: ";
	public static final String USER_ID = "userId";
	public static final String ADMIN_ID = "adminId";

	public static final String TOO_MANY_REQUESTS = "Too many requests from IP: ";
	public static final String OTP_VERIFICATION_SUBJECT = "Your OTP Code - Action Required";

	public static final String REGISTRATION_SUBJECT = "Welcome to Ninja-Map Platform";

	public static final String EMAIL_VERIFICATION_SUBJECT = "Verify Your Email Address";

	public static final String NOTIFICATION_SUBJECT = "Notification from Ninja-Map Platform";

	public static final String PASSWORD_UPDATE_NOTIFICATION_SUBJECT = "Your Password Has Been Updated Successfully";

	public static final String LOGIN_SUCCESS_NOTIFICATION_SUBJECT = "Login Successful - Ninja-Map";
	
	
	
  // ======================== CATEGORY ====================================
	
  public static final String CATEGORY_PICTURE = "Category_Picture";
  public static final String ADD_CATEGORY = "Category created successfully";
  public static final String CATEGORY_DELETED = "Category deleted successfully";
}
