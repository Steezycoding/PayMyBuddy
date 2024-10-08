package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RelationshipControllerTests {
	private MockMvc mockMvc;

	private RelationshipController relationshipController;

	@BeforeEach
	void setUp() {
		relationshipController = new RelationshipController();
		mockMvc = MockMvcBuilders.standaloneSetup(relationshipController).build();
	}

	@Test
	@DisplayName("GET /relationships : Should return the 'main-template' view with 'relationships' view fragment")
	void shouldReturnTheRelationshipsView() throws Exception {
		mockMvc.perform(get("/relationships"))
				.andExpect(status().isOk())
				.andExpect(view().name("main-template"))
				.andExpect(model().attributeExists("title"))
				.andExpect(model().attribute("title", "Relationships"))
				.andExpect(model().attributeExists("view"))
				.andExpect(model().attribute("view", "relationships"));
	}
}
