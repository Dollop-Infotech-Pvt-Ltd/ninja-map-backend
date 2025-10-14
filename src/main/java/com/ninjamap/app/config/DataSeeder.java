package com.ninjamap.app.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.PermissionType;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Permission;
import com.ninjamap.app.model.PersonalInfo;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.repository.IAdminRepository;
import com.ninjamap.app.repository.IPermissionRepository;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.utils.constants.AppConstants;

@Component
public class DataSeeder implements CommandLineRunner {

	@Autowired
	private IRolesRepository roleRepository;

	@Autowired
	private IPermissionRepository permissionRepository;

	@Autowired
	private IAdminRepository adminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		Roles adminRole = createAdminRoleWithDefaultPermissions();
		createDefaultAdmin(adminRole);
		// Create user role with no permissions
		createDefaultUserRole();
	}

	private Roles createAdminRoleWithDefaultPermissions() {
		if (roleRepository.existsByRoleNameAndIsDeletedFalse("ADMIN")) {
			return roleRepository.findByRoleName("ADMIN").orElseThrow();
		}
		List<Permission> permissions = List.of(
				// USER MANAGEMENT
				createPermission("USER_MANAGEMENT", PermissionType.READ, "VIEW_USERS"),
				createPermission("USER_MANAGEMENT", PermissionType.WRITE, "CREATE_USERS"),
				createPermission("USER_MANAGEMENT", PermissionType.WRITE, "EDIT_USERS"),
				createPermission("USER_MANAGEMENT", PermissionType.DELETE, "DELETE_USERS"),
				createPermission("USER_MANAGEMENT", PermissionType.READ, "EXPORT_USER_DATA"),
				createPermission("USER_MANAGEMENT", PermissionType.WRITE, "IMPORT_USERS"),

				// ROLE MANAGEMENT
				createPermission("ROLE_MANAGEMENT", PermissionType.READ, "VIEW_ROLES"),
				createPermission("ROLE_MANAGEMENT", PermissionType.WRITE, "CREATE_ROLES"),
				createPermission("ROLE_MANAGEMENT", PermissionType.WRITE, "EDIT_ROLES"),
				createPermission("ROLE_MANAGEMENT", PermissionType.WRITE, "ASSIGN_ROLES"),
				createPermission("ROLE_MANAGEMENT", PermissionType.DELETE, "DELETE_ROLES"),

				// SESSION MANAGEMENT
				createPermission("SESSION_MANAGEMENT", PermissionType.READ, "VIEW_SESSION"),
				createPermission("SESSION_MANAGEMENT", PermissionType.WRITE, "LOGOUT_SESSION"),

				// PERMISSION MANAGEMENT
				createPermission("PERMISSION_MANAGEMENT", PermissionType.READ, "VIEW_PERMISSIONS"),
				createPermission("PERMISSION_MANAGEMENT", PermissionType.WRITE, "CREATE_PERMISSIONS"),
				createPermission("PERMISSION_MANAGEMENT", PermissionType.WRITE, "EDIT_PERMISSIONS"),
				createPermission("PERMISSION_MANAGEMENT", PermissionType.DELETE, "DELETE_PERMISSIONS"),

				// ANALYTICS & REPORTS
				createPermission("ANALYTICS_REPORTS", PermissionType.READ, "VIEW_ANALYTICS"),
				createPermission("ANALYTICS_REPORTS", PermissionType.READ, "ADVANCED_ANALYTICS"),
				createPermission("ANALYTICS_REPORTS", PermissionType.READ, "EXPORT_REPORTS"),
				createPermission("ANALYTICS_REPORTS", PermissionType.WRITE, "CREATE_REPORTS"),
				createPermission("ANALYTICS_REPORTS", PermissionType.READ, "REAL_TIME_DATA"),

				// NAVIGATION MANAGEMENT
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.READ, "VIEW_ROUTES"),
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.WRITE, "CREATE_ROUTES"),
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.WRITE, "EDIT_ROUTES"),
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.DELETE, "DELETE_ROUTES"),
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.WRITE, "ROUTE_OPTIMIZATION"),
				createPermission("NAVIGATION_MANAGEMENT", PermissionType.WRITE, "PUBLISH_ROUTES"),

				// SYSTEM ADMINISTRATION
				createPermission("SYSTEM_ADMINISTRATION", PermissionType.READ, "VIEW_LOGS"),
				createPermission("SYSTEM_ADMINISTRATION", PermissionType.WRITE, "SYSTEM_SETTINGS"),
				createPermission("SYSTEM_ADMINISTRATION", PermissionType.WRITE, "BACKUP_MANAGEMENT"),
				createPermission("SYSTEM_ADMINISTRATION", PermissionType.WRITE, "MAINTENANCE_MODE"),
				createPermission("SYSTEM_ADMINISTRATION", PermissionType.WRITE, "API_INTEGRATIONS"),

				// ABOUT US MANAGEMENT
				createPermission("ABOUT_US_MANAGEMENT", PermissionType.READ, "CREATE_ABOUT_US"),
				createPermission("ABOUT_US_MANAGEMENT", PermissionType.WRITE, "EDIT_ABOUT_US"),

				// ADMIN MANAGEMENT
				createPermission("ADMIN_MANAGEMENT", PermissionType.READ, "VIEW_ADMINS"),
				createPermission("ADMIN_MANAGEMENT", PermissionType.WRITE, "CREATE_ADMINS"),
				createPermission("ADMIN_MANAGEMENT", PermissionType.WRITE, "EDIT_ADMINS"),
				createPermission("ADMIN_MANAGEMENT", PermissionType.DELETE, "DELETE_ADMINS"),

				// BLOG POST MANAGEMENT
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.READ, "VIEW_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.WRITE, "CREATE_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.WRITE, "EDIT_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.DELETE, "DELETE_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.WRITE, "LIKE_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.WRITE, "SHARE_BLOGS"),
				createPermission("BLOG_POST_MANAGEMENT", PermissionType.WRITE, "SAVE_BLOGS"),

				createPermission("COMMENT_MANAGEMENT", PermissionType.WRITE, "CREATE_COMMENT"),
				createPermission("COMMENT_MANAGEMENT", PermissionType.READ, "VIEW_COMMENT"),
				createPermission("COMMENT_MANAGEMENT", PermissionType.WRITE, "LIKE_COMMENT"),
				createPermission("COMMENT_MANAGEMENT", PermissionType.DELETE, "DELETE_COMMENT"),

				// CONTACT US MANAGEMENT
				createPermission("CONTACT_US_MANAGEMENT", PermissionType.READ, "VIEW_CONTACT_US"),
				createPermission("CONTACT_US_MANAGEMENT", PermissionType.DELETE, "DELETE_CONTACT_US"),

				// FAQ MANAGEMENT
				createPermission("FAQ_MANAGEMENT", PermissionType.READ, "VIEW_FAQS"),
				createPermission("FAQ_MANAGEMENT", PermissionType.WRITE, "CREATE_FAQS"),
				createPermission("FAQ_MANAGEMENT", PermissionType.WRITE, "EDIT_FAQS"),
				createPermission("FAQ_MANAGEMENT", PermissionType.DELETE, "DELETE_FAQS"),

				// POLICY MANAGEMENT
				createPermission("POLICY_MANAGEMENT", PermissionType.READ, "CREATE_POLICY_DOCUMENTS"),
				createPermission("POLICY_MANAGEMENT", PermissionType.WRITE, "VIEW_POLICY_DOCUMENTS"),
				createPermission("POLICY_MANAGEMENT", PermissionType.WRITE, "UPDATE_POLICY_DOCUMENTS"),
				createPermission("POLICY_MANAGEMENT", PermissionType.DELETE, "DELETE_POLICY_DOCUMENTS"));

		// Save all permissions first
		List<Permission> savedPermissions = permissionRepository.saveAll(permissions);

		// Assign saved permissions to role
		Roles adminRole = Roles.builder().roleName("ADMIN").description("Administrator role with all permissions")
				.permissions(savedPermissions).build();

		return roleRepository.save(adminRole);
	}

	private void createDefaultAdmin(Roles adminRole) {
		boolean adminExists = adminRepository.findByEmailAndOptionalIsActive(AppConstants.ADMIN_EMAIL, true)
				.isPresent();
		if (adminExists)
			return;

		// Map DTO to entity using embedded PersonalInfo
		PersonalInfo personalInfo = PersonalInfo.builder().firstName("Admin").lastName("Admin")
				.mobileNumber("9012345678").email(AppConstants.ADMIN_EMAIL)
				.password(passwordEncoder.encode(AppConstants.ADMIN_PASSWORD)).profilePicture(null).build();

		Admin admin = Admin.builder().personalInfo(personalInfo).role(adminRole).employeeId("EMP001").build();

		adminRepository.save(admin);
		System.out.println(AppConstants.ADMIN_CREATED_SUCCESSFULLY);
	}

	private void createDefaultUserRole() {
		String roleName = "USER";

		if (roleRepository.existsByRoleNameAndIsDeletedFalse(roleName))
			return;

		Roles userRole = Roles.builder().roleName(roleName).description("Default user role with no permissions")
				.permissions(new ArrayList<>()) // No permissions
				.build();

		roleRepository.save(userRole);
		System.out.println("USER role created successfully.");
	}

	/**
	 * Helper method to create Permission with authority
	 */
	private Permission createPermission(String resource, PermissionType type, String action) {
		return Permission.of(resource, type, action);
	}
}
