package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.exception.UserNotFoundException;
import com.paymybuddy.webapp.exception.UserRelationshipAlreadyExistsException;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.model.UserRelationshipId;
import com.paymybuddy.webapp.service.UserRelationshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserRelationshipControllerTests {
	private MockMvc mockMvc;

	@Mock
	private UserRelationshipService userRelationshipService;

	@InjectMocks
	private UserRelationshipController userRelationshipController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		userRelationshipController = new UserRelationshipController(userRelationshipService);
		mockMvc = MockMvcBuilders.standaloneSetup(userRelationshipController).build();
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

		private final UserRelationship dummyRelationship = UserRelationship.builder()
				.userId(new User(1L, "j.smith", "j.smith@email.com", "password", "USER", 100f))
				.relationUserId(new User(2L, "j.doe", validEmail, "password", "USER", 50f))
				.build();

		@Test
		@DisplayName("Should submit form with valid email format")
		void shouldSubmitFormWithValidEmailFormat() throws Exception {
			when(userRelationshipService.addRelationship(anyString())).thenReturn(dummyRelationship);

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

		@Test
		@DisplayName("Should add new relation if relation email exists")
		void shouldAddRelationIfRelationEmailExists() throws Exception {

			when(userRelationshipService.addRelationship(anyString())).thenReturn(dummyRelationship);

			mockMvc.perform(post("/relationships")
							.param("email", validEmail))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/relationships"))
					.andExpect(flash().attributeExists("alert"))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo("success"))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Relation " + validEmail + " added successfully"))));

			verify(userRelationshipService, times(1)).addRelationship(eq(validEmail));
		}

		@Test
		@DisplayName("Should NOT add new relation if CURRENT user NOT retrieved")
		void shouldNotAddRelationIfCurrentUserNotRetrieved() throws Exception {
			doThrow(new UserContextNotFoundException()).when(userRelationshipService).addRelationship(anyString());

			mockMvc.perform(post("/relationships")
						.param("email", validEmail))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo("danger"))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Authenticated user cannot be found."))));

			verify(userRelationshipService, times(1)).addRelationship(eq(validEmail));
		}

		@Test
		@DisplayName("Should NOT add new relation if RELATION user NOT found")
		void shouldNotAddRelationIfRelationUserNotFound() throws Exception {
			doThrow(new UserNotFoundException(validEmail)).when(userRelationshipService).addRelationship(anyString());

			mockMvc.perform(post("/relationships")
							.param("email", validEmail))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo("danger"))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo(String.format("User with email '%s' doesn't exist.", validEmail)))));

			verify(userRelationshipService, times(1)).addRelationship(eq(validEmail));
		}

		@Test
		@DisplayName("Should NOT add new relation if Relationship already exists")
		void shouldNotAddRelationIfRelationshipExists() throws Exception {
			UserRelationshipId relationshipId = new UserRelationshipId(dummyRelationship.getUserId(), dummyRelationship.getRelationUserId());

			doThrow(new UserRelationshipAlreadyExistsException(relationshipId)).when(userRelationshipService).addRelationship(anyString());

			mockMvc.perform(post("/relationships")
							.param("email", validEmail))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo("warning"))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo(String.format(
							"Relationship between %s and %s already exists.",
							dummyRelationship.getUserId().getEmail(),
							dummyRelationship.getRelationUserId().getEmail()
					)))));

			verify(userRelationshipService, times(1)).addRelationship(eq(validEmail));
		}
	}
}
