package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

	@BeforeEach
	void setUp() {
		// Manual set up of Security Context for all tests
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(dummyUser.getEmail(), dummyUser.getPassword(), null);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Nested
	@DisplayName("getUserByEmail() Tests")
	class GetUserByEmailTests {
		@Test
		@DisplayName("Should return empty if email NOT exists")
		void givenEmailNotExists_whenGetByEmail_thenReturnEmpty() {
			when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

			Optional<User> actual = userService.getUserByEmail("not_exists@email.com");

			verify(userRepository, times(1)).findByEmail(eq("not_exists@email.com"));
			assertThat(actual).isEmpty();
		}

		@Test
		@DisplayName("Should return User if email exists")
		void givenEmailExists_whenGetByEmail_thenReturnUser() {
			when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(dummyUser));

			Optional<User> actual = userService.getUserByEmail("j.doe@email.com");

			verify(userRepository, times(1)).findByEmail(eq("j.doe@email.com"));
			assertThat(actual).isNotEmpty();
			assertThat(Optional.of(actual)).hasValue(Optional.of(dummyUser));
		}
	}

	@Nested
	@DisplayName("getCurrentUser() Tests")
	class GetCurrentUserTests {
		@Test
		@DisplayName("Should return user if authentication exists")
		void givenAuthenticationExists_whenGetCurrentUser_thenReturnUser() {
			when(userService.getUserByEmail(dummyUser.getEmail())).thenReturn(Optional.of(dummyUser));

			Optional<User> result = userService.getCurrentUser();

			assertThat(result).isPresent();
			assertThat(result.get()).isEqualTo(dummyUser);
		}

		@Test
		@DisplayName("Should return empty if authentication NOT exists")
		void givenAuthenticationNotExists_whenGetCurrentUser_thenReturnEmpty() {
			SecurityContextHolder.clearContext();

			Optional<User> result = userService.getCurrentUser();

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("addUser() Tests")
	class AddUserTests {
		@Test
		@DisplayName("Should add User")
		void givenValidUser_whenAddUser_thenAddSuccessfully() {
			when(userRepository.save(any(User.class))).thenReturn(dummyUser);

			User actual = userService.addUser(dummyUser);

			verify(userRepository, times(1)).save(dummyUser);
			assertThat(actual).isNotNull();
			assertThat(actual).isEqualTo(dummyUser);
		}
	}

	@Nested
	@DisplayName("userExists() Tests")
	class UserExistsTests {
		@Test
		@DisplayName("Should return 'true' if User already exists")
		void givenExistingUser_whenUserExists_thenReturnTrue() {
			when(userRepository.findByEmail(dummyUser.getEmail())).thenReturn(Optional.of(dummyUser));

			boolean actual = userService.userExists(dummyUser);

			verify(userRepository, times(1)).findByEmail(eq(dummyUser.getEmail()));
			assertThat(actual).isTrue();
		}

		@Test
		@DisplayName("Should return 'false' if User not exists")
		void givenNonExistingUser_whenUserExists_thenReturnFalse() {
			when(userRepository.findByEmail(dummyUser.getEmail())).thenReturn(Optional.empty());

			boolean actual = userService.userExists(dummyUser);

			verify(userRepository, times(1)).findByEmail(eq(dummyUser.getEmail()));
			assertThat(actual).isFalse();
		}
	}

	@Nested
	@DisplayName("updateUser() Tests")
	class UpdateUserTests {
		@Test
		@DisplayName("Should update User")
		void givenValidUser_whenUpdateUser_thenUpdateSuccessfully() {
			User updatedUser = User.builder()
					.id(1L)
					.username("updated.username")
					.email("updated.username@email.com")
					.password("updatedPassword")
					.role("USER")
					.build();

			when(userRepository.save(any(User.class))).thenReturn(updatedUser);

			userService.updateUser(updatedUser);

			Authentication updatedAuthentication = SecurityContextHolder.getContext().getAuthentication();

			assertThat(updatedAuthentication).isNotNull();
			assertThat(updatedAuthentication.getName()).isEqualTo(updatedUser.getEmail());

			verify(userRepository, times(1)).save(updatedUser);
		}
	}
}
