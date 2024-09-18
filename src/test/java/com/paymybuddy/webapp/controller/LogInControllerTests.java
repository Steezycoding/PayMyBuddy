package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LogInControllerTests {

	private MockMvc mockMvc;

	private LogInController logInController;

	@BeforeEach
	void setUp() {
		logInController = new LogInController();
		mockMvc = MockMvcBuilders.standaloneSetup(logInController).build();
	}

	@Test
	@DisplayName("GET /login : Should return the 'main-template' view with 'login' view fragment")
	public void shouldReturnTheLoginView() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("main-template"))
				.andExpect(model().attributeExists("title"))
				.andExpect(model().attribute("title", "Login"))
				.andExpect(model().attributeExists("view"))
				.andExpect(model().attribute("view", "login"));
	}
}
