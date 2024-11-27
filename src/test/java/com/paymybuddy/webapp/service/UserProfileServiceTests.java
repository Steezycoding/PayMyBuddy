package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserProfileServiceTests {

	@Mock
	private UserService userService;

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