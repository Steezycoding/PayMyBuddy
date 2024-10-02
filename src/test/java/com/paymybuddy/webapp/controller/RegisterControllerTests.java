package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.UserRegistrationDTO;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.service.RegisterService;
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

public class RegisterControllerTests {

	private MockMvc mockMvc;

	@Mock
	private RegisterService registerService;

	@InjectMocks
	private RegisterController registerController;

	private UserRegistrationDTO registeringUser;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		registerController = new RegisterController(registerService);
		mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

		registeringUser = UserRegistrationDTO.builder()
				.username("j.doe")
				.email("j.doe@email.com")
				.password("val1dP@ssword")
				.build();
	}

	@Nested
	@DisplayName("GET /register Tests")
	class RegistrationPageTests {
		@Test
		@DisplayName("Should return the 'main-template' view with 'register' view fragment")
		public void shouldReturnTheRegisterView() throws Exception {
			mockMvc.perform(get("/register"))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attributeExists("title"))
					.andExpect(model().attribute("title", "Register"))
					.andExpect(model().attributeExists("view"))
					.andExpect(model().attribute("view", "register"));
		}
	}

	@Nested
	@DisplayName("POST /register Tests")
	class UserRegistrationTests {
		@Test
		@DisplayName("Should add a new user with collected form values")
		public void shouldRegisterNewUserSuccessfully() throws Exception {
			mockMvc.perform(post("/register")
							.param("username", registeringUser.getUsername())
							.param("email", registeringUser.getEmail())
							.param("password", registeringUser.getPassword()))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/login"));

			verify(registerService, times(1)).registerUser(eq(registeringUser));
		}

		@Test
		@DisplayName("Should NOT add new user if USERNAME is not valid")
		public void shouldFailedRegisterNewUserWithBadUsernameFormat() throws Exception {
			registeringUser.setUsername("!invalidUsername%");

			mockMvc.perform(post("/register")
							.param("username", registeringUser.getUsername())
							.param("email", registeringUser.getEmail())
							.param("password", registeringUser.getPassword()))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "register"))
					.andExpect(model().attributeHasFieldErrors("user", "username"));

			verifyNoInteractions(registerService);
		}

		@Test
		@DisplayName("Should NOT add new user if EMAIL is not valid")
		public void shouldFailedRegisterNewUserWithBadEmailFormat() throws Exception {
			// Valid email must be exactly: <local_part>@<sld>.<tld>
			registeringUser.setEmail("invalidEmail");

			mockMvc.perform(post("/register")
							.param("username", registeringUser.getUsername())
							.param("email", registeringUser.getEmail())
							.param("password", registeringUser.getPassword()))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "register"))
					.andExpect(model().attributeHasFieldErrors("user", "email"));

			verifyNoInteractions(registerService);
		}

		@Test
		@DisplayName("Should NOT add new user if PASSWORD is not valid")
		public void shouldFailedRegisterNewUserWithBadPasswordFormat() throws Exception {
			registeringUser.setPassword("invalidPassword");

			mockMvc.perform(post("/register")
							.param("username", registeringUser.getUsername())
							.param("email", registeringUser.getEmail())
							.param("password", registeringUser.getPassword()))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "register"))
					.andExpect(model().attributeHasFieldErrors("user", "password"));

			verifyNoInteractions(registerService);
		}

		@Test
		@DisplayName("Should NOT add new user if User already exists")
		public void shouldFailedRegisterNewUserIfUserExists() throws Exception {
			doThrow(new UserAlreadyExistsException(registeringUser.getEmail())).when(registerService).registerUser(any(UserRegistrationDTO.class));

			mockMvc.perform(post("/register")
							.param("username", registeringUser.getUsername())
							.param("email", registeringUser.getEmail())
							.param("password", registeringUser.getPassword()))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "register"))
					.andExpect(model().attributeHasFieldErrors("user", "email"));

			verify(registerService, times(1)).registerUser(eq(registeringUser));
			verifyNoMoreInteractions(registerService);
		}
	}
}
