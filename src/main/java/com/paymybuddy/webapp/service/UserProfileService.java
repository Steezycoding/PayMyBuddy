package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

	private final UserService userService;

	public UserProfileService(UserService userService) {
		this.userService = userService;
	}

	/***
	 * Gets the current user profile.
	 *
	 * @return the user profile
	 * @throws UserContextNotFoundException if the authenticated user can not be retrieved
	 */
	public UserProfileDTO getUserProfile() {
		User user = userService.getCurrentUser()
				.orElseThrow(() -> new UserContextNotFoundException());

		return UserProfileDTO.builder()
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.build();
	}
}
