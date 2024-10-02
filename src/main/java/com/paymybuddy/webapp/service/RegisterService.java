package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserRegistrationDTO;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
	private final UserService userService;

	private final BCryptPasswordEncoder passwordEncoder;

	public RegisterService(UserService userService, BCryptPasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Register a new User.
	 * Sanitizes (lowercase the email address) and secure (encrypt password) the User entity before calling User service to add the new User.
	 * Also set defaults values ("USER" role and zeroed balance).
	 *
	 * @param registeringUser the UserRegistrationDTO object from Register form
	 *
	 * @throws UserAlreadyExistsException if User already exists
	 *
	 */
	public void registerUser(UserRegistrationDTO registeringUser) {
		String registeringEmail = registeringUser.getEmail().toLowerCase();

		if (userService.userExists(registeringUser.toUser())) {
			throw new UserAlreadyExistsException(registeringEmail);
		}

		User user = User.builder()
				.username(registeringUser.getUsername())
				.email(registeringEmail)
				.password(passwordEncoder.encode(registeringUser.getPassword()))
				.role("USER")
				.balance(0f)
				.build();

		userService.addUser(user);
	}
}
