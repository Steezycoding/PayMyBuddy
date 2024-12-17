package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.exception.RegistrationException;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.exception.UserException;
import com.paymybuddy.webapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserProfileServiceTests {

	@Mock
	private UserService userService;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserProfileService userProfileService;

	private User dummyUserEntity;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		dummyUserEntity = User.builder()
				.id(1L)
				.username("j.doe")
				.email("j.doe@email.com")
				.password("encodedPassword")
				.role("USER")
				.balance(0f)
				.build();
	}

	@Nested
	@DisplayName("getUserProfile() Tests")
	class GetUserProfileTests {
		@Test
		@DisplayName("Should return the user profile information")
		void givenCurrentUserExists_whenGetUserProfile_thenUserProfileDTO() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));

			UserProfileDTO expectedUserProfileDTO = userProfileService.getUserProfile();

			assertThat(expectedUserProfileDTO).isNotNull();
			assertThat(expectedUserProfileDTO.getUsername()).isEqualTo(dummyUserEntity.getUsername());
			assertThat(expectedUserProfileDTO.getEmail()).isEqualTo(dummyUserEntity.getEmail());
			assertThat(expectedUserProfileDTO.getPassword()).isEqualTo(dummyUserEntity.getPassword());

			verify(userService, times(1)).getCurrentUser();
			verifyNoMoreInteractions(userService);
		}
	}

	@Nested
	@DisplayName("updateUser() Tests")
	class UpdateUserTests {
		private UserProfileDTO dummyUserProfileDTO;
		private User dummyExistingUser;

		@BeforeEach
		void setUp() {
			dummyUserProfileDTO = UserProfileDTO.builder()
					.username("john.doe")
					.email("john.doe@email.com")
					.password("NEWSecuredPassword")
					.build();

			dummyExistingUser = User.builder()
					.id(2L)
					.username("k.smith")
					.email("k.smith@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();
		}

		@Test
		@DisplayName("Should update the user profile information")
		void givenCurrentUserExists_whenUpdateUser_thenUserProfileDTO() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));
			when(userService.getUserByEmail(dummyUserProfileDTO.getEmail())).thenReturn(Optional.empty());
			when(passwordEncoder.matches(dummyUserProfileDTO.getPassword(), dummyUserEntity.getPassword())).thenReturn(false);
			when(passwordEncoder.encode(dummyUserProfileDTO.getPassword())).thenReturn("NewENCODEDPassword");

			userProfileService.updateUser(dummyUserProfileDTO);

			assertThat(dummyUserEntity.getUsername()).isEqualTo(dummyUserProfileDTO.getUsername());
			assertThat(dummyUserEntity.getEmail()).isEqualTo(dummyUserProfileDTO.getEmail());
			assertThat(dummyUserEntity.getPassword()).isEqualTo("NewENCODEDPassword");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(dummyUserProfileDTO.getEmail());
			verify(userService, times(1)).updateUser(dummyUserEntity);
		}

		@Test
		@DisplayName("Should NOT update user if current user does not exist")
		void givenCurrentUserDoesNotExist_whenUpdateUser_thenThrowUserContextNotFoundException() {
			when(userService.getCurrentUser()).thenReturn(Optional.empty());

			UserException exception = assertThrows(UserContextNotFoundException.class, () -> {
				userProfileService.updateUser(dummyUserProfileDTO);
			});

			assertThat(exception.getMessage()).isEqualTo("Authenticated user cannot be found.");
			verify(userService, times(1)).getCurrentUser();
			verifyNoMoreInteractions(userService);
		}

		@Test
		@DisplayName("Should NOT update user if new email already exists")
		void givenNewEmailAlreadyExists_whenUpdateUser_thenThrowUserAlreadyExistsException() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));
			when(userService.getUserByEmail(dummyExistingUser.getEmail())).thenReturn(Optional.of(dummyExistingUser));

			dummyUserProfileDTO.setEmail(dummyExistingUser.getEmail());

			RegistrationException exception = assertThrows(UserAlreadyExistsException.class, () -> {
				userProfileService.updateUser(dummyUserProfileDTO);
			});

			String expectedExceptionMessage = String.format("User with email '%s' already exists.", dummyUserProfileDTO.getEmail());

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(dummyUserProfileDTO.getEmail());
			verifyNoMoreInteractions(userService);
		}

		@Test
		@DisplayName("Should not update password if new password is null")
		void givenNullPassword_whenUpdateUser_thenPasswordNotUpdated() {
			dummyUserProfileDTO.setPassword(null);

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));

			userProfileService.updateUser(dummyUserProfileDTO);

			assertThat(dummyUserEntity.getPassword()).isEqualTo("encodedPassword");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).updateUser(dummyUserEntity);
			verify(userService, times(1)).getUserByEmail(dummyUserProfileDTO.getEmail());
			verifyNoMoreInteractions(userService);
			verify(passwordEncoder, never()).matches(anyString(), anyString());
			verify(passwordEncoder, never()).encode(anyString());
			verifyNoMoreInteractions(passwordEncoder);
		}

		@Test
		@DisplayName("Should not update password if new password is empty")
		void givenEmptyPassword_whenUpdateUser_thenPasswordNotUpdated() {
			dummyUserProfileDTO.setPassword("");

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));

			userProfileService.updateUser(dummyUserProfileDTO);

			assertThat(dummyUserEntity.getPassword()).isEqualTo("encodedPassword");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).updateUser(dummyUserEntity);
			verify(passwordEncoder, never()).matches(anyString(), anyString());
			verify(passwordEncoder, never()).encode(anyString());
		}

		@Test
		@DisplayName("Should update password if new password does not match current password")
		void givenNewPasswordDoesNotMatchCurrentPassword_whenUpdateUser_thenPasswordUpdated() {
			String oldPassword = dummyExistingUser.getPassword();

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));
			when(passwordEncoder.matches(dummyUserProfileDTO.getPassword(), dummyUserEntity.getPassword())).thenReturn(false);
			when(passwordEncoder.encode(dummyUserProfileDTO.getPassword())).thenReturn("newEncodedPassword");

			userProfileService.updateUser(dummyUserProfileDTO);

			assertThat(dummyUserEntity.getPassword()).isEqualTo("newEncodedPassword");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(dummyUserProfileDTO.getEmail());
			verify(userService, times(1)).updateUser(dummyUserEntity);
			verifyNoMoreInteractions(userService);
			verify(passwordEncoder, times(1)).matches(dummyUserProfileDTO.getPassword(), oldPassword);
			verify(passwordEncoder, times(1)).encode(dummyUserProfileDTO.getPassword());
		}

		@Test
		@DisplayName("Should not update password if new password matches current password")
		void givenNewPasswordMatchesCurrentPassword_whenUpdateUser_thenPasswordNotUpdated() {
			dummyUserProfileDTO.setPassword(dummyExistingUser.getPassword());

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyUserEntity));
			when(passwordEncoder.matches(dummyUserProfileDTO.getPassword(), dummyUserEntity.getPassword())).thenReturn(true);

			userProfileService.updateUser(dummyUserProfileDTO);

			assertThat(dummyUserEntity.getPassword()).isEqualTo(dummyUserEntity.getPassword());

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).updateUser(dummyUserEntity);
			verify(passwordEncoder, times(1)).matches(dummyUserProfileDTO.getPassword(), dummyUserEntity.getPassword());

		}
	}
}