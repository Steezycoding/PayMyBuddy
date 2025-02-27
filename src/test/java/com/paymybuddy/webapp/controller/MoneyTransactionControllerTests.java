package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import com.paymybuddy.webapp.exception.MoneyTransactionBelowMinimumAmountException;
import com.paymybuddy.webapp.exception.MoneyTransactionExceedsSenderBalanceException;
import com.paymybuddy.webapp.exception.MoneyTransactionNegativeAmountException;
import com.paymybuddy.webapp.exception.UserRelationshipNoRelationException;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.service.MoneyTransactionService;
import com.paymybuddy.webapp.utils.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MoneyTransactionControllerTests {

	private MockMvc mockMvc;

	@Mock
	private MoneyTransactionService moneyTransactionService;

	@InjectMocks
	private MoneyTransactionController moneyTransactionController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		moneyTransactionController = new MoneyTransactionController(moneyTransactionService);
		mockMvc = MockMvcBuilders.standaloneSetup(moneyTransactionController).build();
	}

	@Nested
	@DisplayName("GET /money-transaction Tests")
	class GetMoneyTransactionTests {
		@Test
		@DisplayName("Should return the 'main-template' view with 'money-transactions' view fragment")
		public void shouldReturnTheMoneyTransactionView() throws Exception {
			mockMvc.perform(get("/money-transactions"))
					.andExpect(status().isOk())
					.andExpect(view().name("main-template"))
					.andExpect(model().attributeExists("title"))
					.andExpect(model().attribute("title", "Transactions"))
					.andExpect(model().attributeExists("view"))
					.andExpect(model().attribute("view", "money-transactions"));
		}

		@Test
		@DisplayName("Should get the user's transactions")
		public void shouldGetTheUserTransactionsIfThereAreTransactions() throws Exception {
			List<MoneyTransactionDTO> transactions = new ArrayList<>();
			transactions.add(MoneyTransactionDTO.builder().amount(100.00).description("Test transaction 1").build());
			transactions.add(MoneyTransactionDTO.builder().amount(150.00).description("Test transaction 2").build());
			transactions.add(MoneyTransactionDTO.builder().amount(200.00).description("Test transaction 3").build());

			when(moneyTransactionService.getCurrentUserTransactions()).thenReturn(transactions);

			mockMvc.perform(get("/money-transactions"))
					.andExpect(status().isOk())
					.andExpect(model().attributeExists("transactions"))
					.andExpect(model().attribute("transactions", transactions));

			verify(moneyTransactionService, times(1)).getCurrentUserTransactions();
		}

		@Test
		@DisplayName("Should get the user's relationships")
		public void shouldGetTheUserRelationshipsIfThereAreRelationships() throws Exception {
			List<UserContactDTO> contacts = new ArrayList<>();
			contacts.add(UserContactDTO.builder().id(2L).username("j.doe").email("j.doe@email.com").build());
			contacts.add(UserContactDTO.builder().id(3L).username("k.epf").email("k.epf@email.com").build());
			contacts.add(UserContactDTO.builder().id(4L).username("i.cnd").email("i.cnd@email.com").build());

			when(moneyTransactionService.getUserRelationships()).thenReturn(contacts);

			mockMvc.perform(get("/money-transactions"))
					.andExpect(status().isOk())
					.andExpect(model().attributeExists("contacts"))
					.andExpect(model().attribute("contacts", contacts));

			verify(moneyTransactionService, times(1)).getUserRelationships();
		}

		@Test
		@DisplayName("Should handle exception and redirect to /relationships if user has NO relationship")
		public void shouldHandleExceptionAndRedirectToRelationshipsIfThereAreNoRelationship() throws Exception {
			User fakeCurrentUser = new User();
			fakeCurrentUser.setId(1L);
			fakeCurrentUser.setUsername("DummyAuthUser");

			doThrow(new UserRelationshipNoRelationException(fakeCurrentUser)).when(moneyTransactionService).getUserRelationships();

			mockMvc.perform(get("/money-transactions"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/relationships"))
					.andExpect(flash().attributeExists("alert"))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo(Alert.AlertType.WARNING))))
					.andExpect(flash().attribute("alert", hasProperty("message", equalTo("You have no contact available for transactions. Please add a contact to initiate a payment."))));

			verify(moneyTransactionService, times(1)).getUserRelationships();
		}
	}

	@Nested
	@DisplayName("POST /money-transaction Tests")
	class PostMoneyTransactionTests {
		private final MoneyTransactionDTO validTransactionDTO = MoneyTransactionDTO.builder()
				.amount(150.00)
				.description("Test transaction description")
				.receiverUsername("k.epf")
				.receiverEmail("k.epf@email.com")
				.build();

		@Test
		@DisplayName("Should create a transaction between users")
		void shouldCreateTransactionBetweenCurrentUserAndRelation() throws Exception {
			mockMvc.perform(post("/money-transactions")
							.param("receiverEmail", validTransactionDTO.getReceiverEmail())
							.param("receiverUsername", validTransactionDTO.getReceiverUsername())
							.param("description", validTransactionDTO.getDescription())
							.param("amount", String.valueOf(validTransactionDTO.getAmount())))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/money-transactions"))
					.andExpect(flash().attributeExists("alert"))
					.andExpect(flash().attribute("alert", hasProperty("type", equalTo(Alert.AlertType.SUCCESS))))
					.andExpect(flash().attribute("alert", hasProperty("message",
							equalTo(String.format("The payment was successfully sent to %s.", validTransactionDTO.getReceiverUsername()))
					)));

			verify(moneyTransactionService, times(1)).createMoneyTransaction(eq(validTransactionDTO));
		}

		@Nested
		@DisplayName("Form Validation Tests")
		class FormValidationTests {
			@Test
			@DisplayName("Should NOT submit form and display errors with empty receiver email")
			void shouldNotSubmitFormWithEmptyReceiverEmail() throws Exception {
				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", "")
								.param("receiverUsername", validTransactionDTO.getReceiverUsername())
								.param("description", validTransactionDTO.getDescription())
								.param("amount", String.valueOf(validTransactionDTO.getAmount())))
						.andExpect(view().name("main-template"))
						.andExpect(model().attribute("view", "money-transactions"))
						.andExpect(model().attributeHasFieldErrors("transaction", "receiverEmail"));
			}

			@Test
			@DisplayName("Should NOT submit form and display errors with empty receiver username")
			void shouldNotSubmitFormWithEmptyReceiverUsername() throws Exception {
				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", validTransactionDTO.getReceiverEmail())
								.param("receiverUsername", "")
								.param("description", validTransactionDTO.getDescription())
								.param("amount", String.valueOf(validTransactionDTO.getAmount())))
						.andExpect(view().name("main-template"))
						.andExpect(model().attribute("view", "money-transactions"))
						.andExpect(model().attributeHasFieldErrors("transaction", "receiverUsername"));
			}

			@Test
			@DisplayName("Should NOT submit form and display errors with empty amount")
			void shouldNotSubmitFormWithEmptyAmount() throws Exception {
				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", validTransactionDTO.getReceiverEmail())
								.param("receiverUsername", validTransactionDTO.getReceiverUsername())
								.param("description", validTransactionDTO.getDescription())
								.param("amount", ""))
						.andExpect(view().name("main-template"))
						.andExpect(model().attribute("view", "money-transactions"))
						.andExpect(model().attributeHasFieldErrors("transaction", "amount"));
			}
		}

		@Test
		@DisplayName("Should NOT submit form and display errors with invalid receiver email")
		void shouldNotSubmitFormWithInvalidReceiverId() throws Exception {
			mockMvc.perform(post("/money-transactions")
					.param("receiverEmail", "")
					.param("receiverUsername", validTransactionDTO.getReceiverUsername())
					.param("description", validTransactionDTO.getDescription())
					.param("amount", String.valueOf(validTransactionDTO.getAmount())))
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "money-transactions"))
					.andExpect(model().attributeHasFieldErrors("transaction", "receiverEmail"));
		}

		@Test
		@DisplayName("Should NOT submit form and display errors with invalid receiver username")
		void shouldNotSubmitFormWithInvalidReceiverUsername() throws Exception {
			mockMvc.perform(post("/money-transactions")
							.param("receiverEmail", validTransactionDTO.getReceiverEmail())
							.param("receiverUsername", "")
							.param("description", validTransactionDTO.getDescription())
							.param("amount", "100"))
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "money-transactions"))
					.andExpect(model().attributeHasFieldErrors("transaction", "receiverUsername"));
		}

		@Test
		@DisplayName("Should NOT submit form and display errors with invalid amount")
		void shouldNotSubmitFormWithInvalidAmount() throws Exception {
			mockMvc.perform(post("/money-transactions")
							.param("receiverEmail", validTransactionDTO.getReceiverEmail())
							.param("receiverUsername", validTransactionDTO.getReceiverUsername())
							.param("description", validTransactionDTO.getDescription())
							.param("amount", "0.99"))
					.andExpect(view().name("main-template"))
					.andExpect(model().attribute("view", "money-transactions"))
					.andExpect(model().attributeHasFieldErrors("transaction", "amount"));
		}

		@Nested
		@DisplayName("Exception Handling Tests")
		class ExceptionHandlingTests {
			@Mock
			private Validator validator;

			private final MoneyTransactionDTO invalidAmountTransactionDTO = MoneyTransactionDTO.builder()
					.description("Test transaction description")
					.receiverUsername("k.epf")
					.receiverEmail("k.epf@email.com")
					.build();

			@BeforeEach
			void setUp() {
				MockitoAnnotations.openMocks(this);
				mockMvc = MockMvcBuilders.standaloneSetup(moneyTransactionController)
						.setValidator(validator)
						.build();
			}

			@Test
			@DisplayName("Should handle exception if transaction amount is negative")
			void shouldHandleExceptionIfTransactionAmountIsNegative() throws Exception {
				invalidAmountTransactionDTO.setAmount(-10.0);
				doThrow(new MoneyTransactionNegativeAmountException())
						.when(moneyTransactionService)
						.createMoneyTransaction(eq(invalidAmountTransactionDTO));

				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", invalidAmountTransactionDTO.getReceiverEmail())
								.param("receiverUsername", invalidAmountTransactionDTO.getReceiverUsername())
								.param("description", invalidAmountTransactionDTO.getDescription())
								.param("amount", String.valueOf(invalidAmountTransactionDTO.getAmount())))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/money-transactions"))
						.andExpect(flash().attributeExists("alert"))
						.andExpect(flash().attribute("alert", hasProperty("type", equalTo(Alert.AlertType.DANGER))))
						.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Transaction amount cannot be negative."))));

				verify(moneyTransactionService, times(1)).createMoneyTransaction(eq(invalidAmountTransactionDTO));
			}

			@Test
			@DisplayName("Should handle exception if transaction amount is below minimum amount")
			void shouldHandleExceptionIfTransactionAmountIsBelowMinimumAmount() throws Exception {
				invalidAmountTransactionDTO.setAmount(0.5);
				doThrow(new MoneyTransactionBelowMinimumAmountException(1.0))
						.when(moneyTransactionService)
						.createMoneyTransaction(eq(invalidAmountTransactionDTO));

				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", invalidAmountTransactionDTO.getReceiverEmail())
								.param("receiverUsername", invalidAmountTransactionDTO.getReceiverUsername())
								.param("description", invalidAmountTransactionDTO.getDescription())
								.param("amount", String.valueOf(invalidAmountTransactionDTO.getAmount())))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/money-transactions"))
						.andExpect(flash().attributeExists("alert"))
						.andExpect(flash().attribute("alert", hasProperty("type", equalTo(Alert.AlertType.DANGER))))
						.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Transaction amount cannot be below the minimum amount."))));

				verify(moneyTransactionService, times(1)).createMoneyTransaction(eq(invalidAmountTransactionDTO));
			}

			@Test
			@DisplayName("Should handle exception if transaction amount exceeds sender balance")
			void shouldHandleExceptionIfTransactionAmountExceedsSenderBalance() throws Exception {
				invalidAmountTransactionDTO.setAmount(999999999.0);
				doThrow(new MoneyTransactionExceedsSenderBalanceException())
						.when(moneyTransactionService)
						.createMoneyTransaction(invalidAmountTransactionDTO);

				mockMvc.perform(post("/money-transactions")
								.param("receiverEmail", invalidAmountTransactionDTO.getReceiverEmail())
								.param("receiverUsername", invalidAmountTransactionDTO.getReceiverUsername())
								.param("description", invalidAmountTransactionDTO.getDescription())
								.param("amount", String.valueOf(invalidAmountTransactionDTO.getAmount())))
						.andExpect(status().is3xxRedirection())
						.andExpect(redirectedUrl("/money-transactions"))
						.andExpect(flash().attributeExists("alert"))
						.andExpect(flash().attribute("alert", hasProperty("type", equalTo(Alert.AlertType.DANGER))))
						.andExpect(flash().attribute("alert", hasProperty("message", equalTo("Transaction amount exceeds your balance."))));

				verify(moneyTransactionService, times(1)).createMoneyTransaction(eq(invalidAmountTransactionDTO));
			}
		}
	}
}
