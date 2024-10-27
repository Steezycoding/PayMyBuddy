package com.paymybuddy.webapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MoneyTransactionControllerTests {

	private MockMvc mockMvc;

	private MoneyTransactionController moneyTransactionController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		moneyTransactionController = new MoneyTransactionController();
		mockMvc = MockMvcBuilders.standaloneSetup(moneyTransactionController).build();
	}

	@Nested
	@DisplayName("GET /money-transaction Tests")
	class GetMoneyTransactionTests {
		@Test
		@DisplayName("Should return the 'main-template' view with 'money-transactions' view fragment")
		public void shouldReturnTheMoneyTransferView() throws Exception {
			mockMvc.perform(get("/money-transactions"))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attributeExists("title"))
					.andExpect(model().attribute("title", "Transactions"))
					.andExpect(model().attributeExists("view"))
					.andExpect(model().attribute("view", "money-transactions"));
		}
	}
}
