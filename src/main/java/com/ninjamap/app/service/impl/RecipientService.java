package com.ninjamap.app.service.impl;

import org.springframework.stereotype.Service;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.User;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.IUserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipientService {

	private final IUserService userService;
	private final IAdminService adminService;

	public User getUser(String userId) {
		return userId != null ? userService.getUserByIdAndIsActive(userId, true) : null;
	}

	public Admin getAdmin(String adminId) {
		return adminId != null ? adminService.getAdminByIdAndIsActive(adminId, true) : null;
	}

	public String getEmail(User user, Admin admin) {
		return user != null ? user.getPersonalInfo().getEmail() : admin != null ? admin.getPersonalInfo().getEmail() : null;
	}

	public String getPhone(User user, Admin admin) {
		return user != null ? user.getPersonalInfo().getMobileNumber() : admin != null ? admin.getPersonalInfo().getMobileNumber() : null;
	}
}
