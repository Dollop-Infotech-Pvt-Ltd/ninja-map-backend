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

		List<Permission> permissions = new ArrayList<>();

		// ====================== USER MANAGEMENT ======================
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.READ).action("VIEW_USERS")
				.build());
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.WRITE)
				.action("CREATE_USERS").build());
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.WRITE).action("EDIT_USERS")
				.build());
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_USERS").build());
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.READ)
				.action("EXPORT_USER_DATA").build());
		permissions.add(Permission.builder().resource("USER_MANAGEMENT").type(PermissionType.WRITE)
				.action("IMPORT_USERS").build());

		// ====================== ROLE MANAGEMENT ======================
		permissions.add(Permission.builder().resource("ROLE_MANAGEMENT").type(PermissionType.READ).action("VIEW_ROLES")
				.build());
		permissions.add(Permission.builder().resource("ROLE_MANAGEMENT").type(PermissionType.WRITE)
				.action("CREATE_ROLES").build());
		permissions.add(Permission.builder().resource("ROLE_MANAGEMENT").type(PermissionType.WRITE).action("EDIT_ROLES")
				.build());
		permissions.add(Permission.builder().resource("ROLE_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_ROLES").build());
		permissions.add(Permission.builder().resource("ROLE_MANAGEMENT").type(PermissionType.WRITE)
				.action("ASSIGN_ROLES").build());

		// ====================== SESSION MANAGEMENT ======================
		permissions.add(Permission.builder().resource("SESSION_MANAGEMENT").type(PermissionType.READ)
				.action("VIEW_SESSION").build());
		permissions.add(Permission.builder().resource("SESSION_MANAGEMENT").type(PermissionType.WRITE)
				.action("LOGOUT_SESSION").build());

		// ====================== POLICY MANAGEMENT ======================
		permissions.add(Permission.builder().resource("PERMISSION_MANAGEMENT").type(PermissionType.READ)
				.action("CREATE_PERMISSIONS").build());
		permissions.add(Permission.builder().resource("PERMISSION_MANAGEMENT").type(PermissionType.WRITE)
				.action("VIEW_PERMISSIONS").build());
		permissions.add(Permission.builder().resource("PERMISSION_MANAGEMENT").type(PermissionType.WRITE)
				.action("EDIT_PERMISSIONS").build());
		permissions.add(Permission.builder().resource("PERMISSION_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_PERMISSIONS").build());

		// ====================== ANALYTICS & REPORTS ======================
		permissions.add(Permission.builder().resource("ANALYTICS_REPORTS").type(PermissionType.READ)
				.action("VIEW_ANALYTICS").build());
		permissions.add(Permission.builder().resource("ANALYTICS_REPORTS").type(PermissionType.READ)
				.action("ADVANCED_ANALYTICS").build());
		permissions.add(Permission.builder().resource("ANALYTICS_REPORTS").type(PermissionType.READ)
				.action("EXPORT_REPORTS").build());
		permissions.add(Permission.builder().resource("ANALYTICS_REPORTS").type(PermissionType.WRITE)
				.action("CREATE_REPORTS").build());
		permissions.add(Permission.builder().resource("ANALYTICS_REPORTS").type(PermissionType.READ)
				.action("REAL_TIME_DATA").build());

		// ====================== NAVIGATION MANAGEMENT ======================
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.READ)
				.action("VIEW_ROUTES").build());
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.WRITE)
				.action("CREATE_ROUTES").build());
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.WRITE)
				.action("EDIT_ROUTES").build());
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_ROUTES").build());
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.WRITE)
				.action("ROUTE_OPTIMIZATION").build());
		permissions.add(Permission.builder().resource("NAVIGATION_MANAGEMENT").type(PermissionType.WRITE)
				.action("PUBLISH_ROUTES").build());

		// ====================== SYSTEM ADMINISTRATION ======================
		permissions.add(Permission.builder().resource("SYSTEM_ADMINISTRATION").type(PermissionType.READ)
				.action("VIEW_LOGS").build());
		permissions.add(Permission.builder().resource("SYSTEM_ADMINISTRATION").type(PermissionType.WRITE)
				.action("SYSTEM_SETTINGS").build());
		permissions.add(Permission.builder().resource("SYSTEM_ADMINISTRATION").type(PermissionType.WRITE)
				.action("BACKUP_MANAGEMENT").build());
		permissions.add(Permission.builder().resource("SYSTEM_ADMINISTRATION").type(PermissionType.WRITE)
				.action("MAINTENANCE_MODE").build());
		permissions.add(Permission.builder().resource("SYSTEM_ADMINISTRATION").type(PermissionType.WRITE)
				.action("API_INTEGRATIONS").build());

		// ====================== ABOUT US MANAGEMENT ======================
		permissions.add(Permission.builder().resource("ABOUT_US_MANAGEMENT").type(PermissionType.READ)
				.action("CREATE_ABOUT_US").build());
		permissions.add(Permission.builder().resource("ABOUT_US_MANAGEMENT").type(PermissionType.WRITE)
				.action("EDIT_ABOUT_US").build());

		// ====================== ADMIN MANAGEMENT ======================
		permissions.add(Permission.builder().resource("ADMIN_MANAGEMENT").type(PermissionType.WRITE)
				.action("CREATE_ADMINS").build());
		permissions.add(Permission.builder().resource("ADMIN_MANAGEMENT").type(PermissionType.READ)
				.action("VIEW_ADMINS").build());
		permissions.add(Permission.builder().resource("ADMIN_MANAGEMENT").type(PermissionType.WRITE)
				.action("EDIT_ADMINS").build());
		permissions.add(Permission.builder().resource("ADMIN_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_ADMINS").build());

		// ====================== BLOG POST MANAGEMENT ======================
		permissions.add(Permission.builder().resource("BLOG_POST_MANAGEMENT").type(PermissionType.READ)
				.action("VIEW_BLOGS").build());
		permissions.add(Permission.builder().resource("BLOG_POST_MANAGEMENT").type(PermissionType.WRITE)
				.action("CREATE_BLOGS").build());
		permissions.add(Permission.builder().resource("BLOG_POST_MANAGEMENT").type(PermissionType.WRITE)
				.action("EDIT_BLOGS").build());
		permissions.add(Permission.builder().resource("BLOG_POST_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_BLOGS").build());

		// ====================== CONTACT US MANAGEMENT ======================
		permissions.add(Permission.builder().resource("CONTACT_US_MANAGEMENT").type(PermissionType.READ)
				.action("VIEW_CONTACT_US").build());
		permissions.add(Permission.builder().resource("CONTACT_US_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_CONTACT_US").build());

		// ====================== FAQ MANAGEMENT ======================
		permissions.add(
				Permission.builder().resource("FAQ_MANAGEMENT").type(PermissionType.READ).action("VIEW_FAQS").build());
		permissions.add(Permission.builder().resource("FAQ_MANAGEMENT").type(PermissionType.WRITE).action("CREATE_FAQS")
				.build());
		permissions.add(
				Permission.builder().resource("FAQ_MANAGEMENT").type(PermissionType.WRITE).action("EDIT_FAQS").build());
		permissions.add(Permission.builder().resource("FAQ_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_FAQS").build());

		// ====================== POLICY MANAGEMENT ======================
		permissions.add(Permission.builder().resource("POLICY_MANAGEMENT").type(PermissionType.READ)
				.action("CREATE_POLICY_DOCUMENTS").build());
		permissions.add(Permission.builder().resource("POLICY_MANAGEMENT").type(PermissionType.WRITE)
				.action("VIEW_POLICY_DOCUMENTS").build());
		permissions.add(Permission.builder().resource("POLICY_MANAGEMENT").type(PermissionType.WRITE)
				.action("UPDATE_POLICY_DOCUMENTS").build());
		permissions.add(Permission.builder().resource("POLICY_MANAGEMENT").type(PermissionType.DELETE)
				.action("DELETE_POLICY_DOCUMENTS").build());

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

		Admin admin = Admin.builder().firstName("Admin").lastName("Admin").mobileNumber("9999999999")
				.email(AppConstants.ADMIN_EMAIL).password(passwordEncoder.encode(AppConstants.ADMIN_PASSWORD))
				.role(adminRole).employeeId("EMP001").build();

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
}
