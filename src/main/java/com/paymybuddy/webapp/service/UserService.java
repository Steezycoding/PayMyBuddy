package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.configuration.security.CustomUserDetailsService;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
		if (Objects.isNull(authentication)) {
			return Optional.empty();
		}
		return getUserByEmail(authentication.getName());
	}

	public User addUser(User user) {
		return userRepository.save(user);
	}

	public void updateUser(User user) {
		userRepository.save(user);
		updateUserSecurityContext(user);
	}

	public boolean userExists(User user) {
		return userRepository.findByEmail(user.getEmail()).isPresent();
	}

	/**
	 * Update the security context with the new user details.
	 * <p><em>Prevents the user from having to log in again in order to update the security context with new user details.</em></p>
	 *
	 * @param user the user with new details
	 */
	private void updateUserSecurityContext(User user) {
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities("ROLE_"  + user.getRole())
				.build();

		UsernamePasswordAuthenticationToken newAuthenticationToken = new UsernamePasswordAuthenticationToken(
				userDetails, user.getPassword(), userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthenticationToken);
	}
}
