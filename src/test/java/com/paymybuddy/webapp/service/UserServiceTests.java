package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test Suite")
public class UserServiceTests {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private static User dummyUser;

	@BeforeAll
	static void setUpAll() {
		dummyUser = User.builder()
				.id(1L)
				.username("j.doe")
				.email("j.doe@email.com")
				.password("password")
				.role("USER")
				.build();
	}

	@Test
	@DisplayName("Get User by email: returns empty if email NOT exists")
	void givenEmailNotExists_whenGetByEmail_thenReturnEmpty() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

		Optional<User> actual = userService.getUserByEmail("not_exists@email.com");

		verify(userRepository, times(1)).findByEmail(eq("not_exists@email.com"));
		assertThat(actual).isEmpty();
	}

	@Test
	@DisplayName("Get User by email: returns User if email exists")
	void givenEmailExists_whenGetByEmail_thenReturnUser() {
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(dummyUser));

		Optional<User> actual = userService.getUserByEmail("j.doe@email.com");

		verify(userRepository, times(1)).findByEmail(eq("j.doe@email.com"));
		assertThat(actual).isNotEmpty();
		assertThat(Optional.of(actual)).hasValue(Optional.of(dummyUser));
	}
}
