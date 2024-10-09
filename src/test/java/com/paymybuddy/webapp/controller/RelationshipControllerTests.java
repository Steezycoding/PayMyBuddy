package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RelationshipControllerTests {
	private MockMvc mockMvc;

	private RelationshipController relationshipController;

	@BeforeEach
	void setUp() {
		relationshipController = new RelationshipController();
		mockMvc = MockMvcBuilders.standaloneSetup(relationshipController).build();
	}

	@Nested
	@DisplayName("GET /relationships Tests")
	class GetRelationshipsTests {
		@Test
		@DisplayName("Should return the 'main-template' view with 'relationships' view fragment")
		void shouldReturnTheRelationshipsView() throws Exception {
			mockMvc.perform(get("/relationships"))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attributeExists("title"))
					.andExpect(model().attribute("title", "Relationships"))
					.andExpect(model().attributeExists("view"))
					.andExpect(model().attribute("view", "relationships"))
					.andExpect(model().attributeExists("emailDTO"));
		}
	}
	@Nested
	@DisplayName("POST /relationships Tests")
	class PostRelationshipsTests {
		private final String validEmail = "j.doe@email.com";
		private final String invalidEmail = "invalidEmail";

		@Test
		@DisplayName("Should submit form with valid email format")
		void shouldSubmitFormWithValidEmailFormat() throws Exception {
			mockMvc.perform(post("/relationships")
							.param("email", validEmail))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/relationships"))
					.andExpect(flash().attributeExists("alert"))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo("success"))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Relation " + validEmail + " added successfully"))));
		}

		@Test
		@DisplayName("Should NOT submit form and display errors with invalid email format")
		void shouldNotSubmitFormWithInvalidEmailFormat() throws Exception {
			mockMvc.perform(post("/relationships")
							.param("email", invalidEmail))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "relationships"))
					.andExpect(model().attributeHasFieldErrors("emailDTO", "email"));
		}
	}
}
