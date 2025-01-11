package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

	private final UserService userService;

	private final BCryptPasswordEncoder passwordEncoder;

	public UserProfileService(UserService userService, BCryptPasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
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

	/***
	 * Update the current user profile.
	 *
	 * @param userDTO the new user profile information
	 * @throws UserContextNotFoundException if the authenticated user can not be retrieved
	 * @throws UserAlreadyExistsException if the new email already exists
	 */
	public void updateUser(UserProfileDTO userDTO) {
		User user = userService.getCurrentUser()
				.orElseThrow(() -> new UserContextNotFoundException());

		user.setUsername(userDTO.getUsername());

		if (!userDTO.getEmail().toLowerCase().equals(user.getEmail())) {
			boolean emailExists = userService.getUserByEmail(userDTO.getEmail()).isPresent();
			if (emailExists) {
				throw new UserAlreadyExistsException(userDTO.getEmail());
			}
			user.setEmail(userDTO.getEmail());
		}

		if (userDTO.getPassword() != null
				&& !userDTO.getPassword().isEmpty()
				&& !passwordEncoder.matches(userDTO.getPassword(), user.getPassword()))
		{
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		userService.updateUser(user);
	}
}
