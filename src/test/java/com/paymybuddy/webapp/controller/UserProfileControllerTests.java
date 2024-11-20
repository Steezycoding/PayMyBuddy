package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserProfileControllerTests {
	private MockMvc mockMvc;

	private UserProfileController userProfileController;

	@BeforeEach
	void setUp() {
		userProfileController = new UserProfileController();
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
					.andExpect(model().attribute("view", "profile"))
					.andExpect(model().attributeExists("user"));
		}
	}
}