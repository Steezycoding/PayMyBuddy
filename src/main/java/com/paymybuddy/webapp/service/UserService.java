package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return getUserByEmail(authentication.getName());
	}

	public User addUser(User user) {
		return userRepository.save(user);
	}

	public boolean userExists(User user) {
		return userRepository.findByEmail(user.getEmail()).isPresent();
	}
}
