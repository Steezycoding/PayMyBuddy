package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegisterControllerTests {

	private MockMvc mockMvc;

	private RegisterController registerController;

	@BeforeEach
	void setUp() {
		registerController = new RegisterController();
		mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();
	}

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
