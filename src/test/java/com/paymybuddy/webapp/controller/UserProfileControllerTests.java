package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserProfileControllerTests {
	private MockMvc mockMvc;

	@Mock
	private UserProfileService userProfileService;

	@InjectMocks
	private UserProfileController userProfileController;


	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		userProfileController = new UserProfileController(userProfileService);
		mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
	}

	@Nested
	@DisplayName("GET /profile Tests")
	class GetProfileTests {

		@Test
		@DisplayName("Should return the 'main-template' view with 'profile' view fragment")
		void shouldReturnTheUserProfileView() throws Exception {
			mockMvc.perform(get("/profile"))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attributeExists("title"))
					.andExpect(model().attribute("title", "Profile"))
					.andExpect(model().attributeExists("view"))
					.andExpect(model().attribute("view", "profile"));
		}

		@Test
		@DisplayName("Should display the auth user information")
		void shouldDisplayTheAuthUserInformation() throws Exception {
			UserProfileDTO dummyUserProfile = UserProfileDTO.builder()
					.username("j.doe")
					.email("j.doe@email.com")
					.password("encodedPassword")
					.build();

			when(userProfileService.getUserProfile()).thenReturn(dummyUserProfile);

			mockMvc.perform(get("/profile"))
					.andExpect(model().attributeExists("user"))
					.andExpect(model().attribute("user", dummyUserProfile));
		}
	}

	@Nested
	@DisplayName("POST /profile Tests")
	class PostProfileTests {
		UserProfileDTO dummyGetUserProfile = UserProfileDTO.builder()
				.username("j.doe")
				.email("j.doe@email.com")
				.password("encodedPassword")
				.build();

		UserProfileDTO dummyUpdatedUserProfile = UserProfileDTO.builder()
				.username("updatedUsername")
				.email("updated@email.com")
				.password("val1dPUpdAt€dP@ssword")
				.build();

		@Test
		@DisplayName("Should update the user profile")
		void shouldUpdateTheUserProfile() throws Exception {
			when(userProfileService.getUserProfile()).thenReturn(dummyGetUserProfile);

			mockMvc.perform(post("/profile")
						.param("username", dummyUpdatedUserProfile.getUsername())
						.param("email", dummyUpdatedUserProfile.getEmail())
						.param("password", dummyUpdatedUserProfile.getPassword()))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/profile"));

			verify(userProfileService, times(1)).updateUser(eq(dummyUpdatedUserProfile));
		}
	}
}
