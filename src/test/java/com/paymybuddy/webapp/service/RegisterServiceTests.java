package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserRegistrationDTO;
import com.paymybuddy.webapp.exception.RegistrationException;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService Test Suite")
public class RegisterServiceTests {
	@Mock
	private UserService userService;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private RegisterService registerService;

	private static UserRegistrationDTO dummyUserRegistration;

	@BeforeAll
	static void setUpAll() {
		dummyUserRegistration = UserRegistrationDTO.builder()
				.username("j.doe")
				.email("j.DOE@Email.com")
				.password("val1d@Password")
				.build();
	}

	@Nested
	@DisplayName("registerUser() Tests")
	class RegisterUserTests {
		@Test
		@DisplayName("Should call User service with converted UserRegistrationDTO")
		void givenUserRegistrationDTO_whenRegisterUser_thenUserServiceCalledWithAddUser() {
			when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

			User expectedUser = User.builder()
					.username(dummyUserRegistration.getUsername())
					.email(dummyUserRegistration.getEmail().toLowerCase())
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();

			registerService.registerUser(dummyUserRegistration);

			verify(userService, times(1)).addUser(eq(expectedUser));
		}

		@Test
		@DisplayName("Should NOT add new User if User already exists")
		void givenUserAlreadyExists_whenRegisterUser_thenUserServiceNotCalled() {
			String lowerCaseEmail = dummyUserRegistration.getEmail().toLowerCase();

			when(userService.userExists(any(User.class))).thenReturn(true);

			RegistrationException exception = assertThrows(UserAlreadyExistsException.class, () -> {
				registerService.registerUser(dummyUserRegistration);
			});

			String expectedExceptionMessage = String.format("User with email '%s' already exists.", lowerCaseEmail);

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(userService, times(1)).userExists(eq(dummyUserRegistration.toUser()));
			verifyNoMoreInteractions(userService);
		}
	}
}
