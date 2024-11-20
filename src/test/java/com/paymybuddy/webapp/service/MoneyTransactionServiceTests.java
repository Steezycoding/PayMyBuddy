package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import com.paymybuddy.webapp.exception.MoneyTransactionBelowMinimumAmountException;
import com.paymybuddy.webapp.exception.MoneyTransactionExceedsSenderBalanceException;
import com.paymybuddy.webapp.exception.MoneyTransactionNegativeAmountException;
import com.paymybuddy.webapp.model.MoneyTransaction;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.MoneyTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MoneyTransactionService Test Suite")
class MoneyTransactionServiceTests {
	@Mock
	private UserRelationshipService userRelationshipService;

	@Mock
	private UserService userService;

	@Mock
	private MoneyTransactionRepository moneyTransactionRepository;

	@InjectMocks
	private MoneyTransactionService moneyTransactionService;

	@Nested
	@DisplayName("getUserRelationships() Tests")
	class GetUserRelationshipsTests {
		private User dummyRelationOne;
		private User dummyRelationTwo;
		private final List<User> dummyRelationships = new ArrayList<>();

		@BeforeEach
		void setUp() {
			dummyRelationOne = User.builder()
					.username("k.epf")
					.email("k.epf@email.com")
					.password("val1d@Password")
					.role("USER")
					.balance(50f)
					.build();

			dummyRelationTwo = User.builder()
					.username("i.cnd")
					.email("i.cnd@email.com")
					.password("val1d@Password")
					.role("USER")
					.balance(0f)
					.build();

			dummyRelationships.add(dummyRelationOne);
			dummyRelationships.add(dummyRelationTwo);
		}

		@Test
		@DisplayName("Should return relationship users if relationships exist")
		void givenRelationshipsServiceReturnsRelations_whenGetUserRelationships_thenReturnUserContacts() {
			when(userRelationshipService.getRelationships()).thenReturn(dummyRelationships);

			List<UserContactDTO> contacts = moneyTransactionService.getUserRelationships();

			assertThat(contacts).isNotNull();
			assertThat(contacts).hasSize(2);
			assertThat(contacts.get(0).getId()).isEqualTo(dummyRelationOne.getId());
			assertThat(contacts.get(0).getUsername()).isEqualTo(dummyRelationOne.getUsername());
			assertThat(contacts.get(0).getEmail()).isEqualTo(dummyRelationOne.getEmail());
			assertThat(contacts.get(1).getId()).isEqualTo(dummyRelationTwo.getId());
			assertThat(contacts.get(1).getUsername()).isEqualTo(dummyRelationTwo.getUsername());
			assertThat(contacts.get(1).getEmail()).isEqualTo(dummyRelationTwo.getEmail());
			verify(userRelationshipService, times(1)).getRelationships();
			verifyNoMoreInteractions(userRelationshipService);
		}
	}

	@Nested
	@DisplayName("getUserTransactions() Tests")
	class GetUserTransactionsTests {
		private User dummySender;
		private User dummyReceiver;

		@BeforeEach
		void setUp() {
			dummySender = User.builder()
					.id(1L)
					.username("j.doe")
					.email("j.doe@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(100f)
					.build();

			dummyReceiver = User.builder()
					.id(2L)
					.username("k.epf")
					.email("k.epf@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();
		}

		@Test
		@DisplayName("Should return user transactions if transactions exist")
		void givenUserTransactionsExist_whenGetUserTransactions_thenReturnUserTransactions() {
			List<MoneyTransaction> dummyTransactions = new ArrayList<>();
			MoneyTransaction dummyTransactionOne = MoneyTransaction.builder()
					.senderId(dummySender)
					.receiverId(dummyReceiver)
					.amount(25.75)
					.description("Payment for services")
					.build();

			MoneyTransaction dummyTransactionTwo = MoneyTransaction.builder()
					.senderId(dummySender)
					.receiverId(dummyReceiver)
					.amount(50.0)
					.description("Payment for goods")
					.build();

			dummyTransactions.add(dummyTransactionOne);
			dummyTransactions.add(dummyTransactionTwo);

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummySender));
			when(moneyTransactionRepository.findBySenderId(any(User.class))).thenReturn(dummyTransactions);

			List<MoneyTransactionDTO> expectedTransactions = moneyTransactionService.getCurrentUserTransactions();

			assertThat(expectedTransactions).isNotNull();
			assertThat(expectedTransactions).hasSize(2);
			verify(userService, times(1)).getCurrentUser();
			verify(moneyTransactionRepository, times(1)).findBySenderId(eq(dummySender));
		}

		@Test
		@DisplayName("Should empty list if transactions NOT exist")
		void givenUserTransactionsNotExist_whenGetUserTransactions_thenReturnEmptyList() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(dummySender));
			when(moneyTransactionRepository.findBySenderId(any(User.class))).thenReturn(new ArrayList<>());

			List<MoneyTransactionDTO> expectedTransactions = moneyTransactionService.getCurrentUserTransactions();

			assertThat(expectedTransactions).isNotNull();
			assertThat(expectedTransactions).hasSize(2);
			verify(userService, times(1)).getCurrentUser();
			verify(moneyTransactionRepository, times(1)).findBySenderId(eq(dummySender));
		}
	}

	@Nested
	@DisplayName("createMoneyTransaction() Tests")
	class CreateMoneyTransactionTests {
		private User dummyAuthUser;
		private User dummyReceiverUser;
		private MoneyTransactionDTO dummyTransactionDTO;

		@BeforeEach
		void setUp() {
			dummyAuthUser = User.builder()
					.id(1L)
					.username("j.doe")
					.email("j.doe@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(100f)
					.build();

			dummyReceiverUser = User.builder()
					.id(2L)
					.username("k.epf")
					.email("k.epf@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();

			dummyTransactionDTO = MoneyTransactionDTO.builder()
					.receiverUsername("k.epf")
					.receiverEmail("k.epf@email.com")
					.amount(25.75)
					.description("Payment for services")
					.build();
		}

		@Test
		@DisplayName("Should create a money transaction")
		void givenAuthUserExistsAndValidMoneyTransaction_whenCreateMoneyTransaction_thenCreateTransaction() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyAuthUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(dummyReceiverUser));

			Float oldAuthUserBalance = dummyAuthUser.getBalance();
			Float oldReceiverUserBalance = dummyReceiverUser.getBalance();

			MoneyTransaction expectedTransaction = MoneyTransaction.builder()
					.senderId(dummyAuthUser)
					.receiverId(dummyReceiverUser)
					.amount(dummyTransactionDTO.getAmount())
					.description(dummyTransactionDTO.getDescription())
					.build();

			moneyTransactionService.createMoneyTransaction(dummyTransactionDTO);

			assertThat(dummyAuthUser.getBalance()).isEqualTo(oldAuthUserBalance - dummyTransactionDTO.getAmount().floatValue());
			assertThat(dummyReceiverUser.getBalance()).isEqualTo(oldReceiverUserBalance + dummyTransactionDTO.getAmount().floatValue());

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(dummyTransactionDTO.getReceiverEmail()));
			verify(userService, times(1)).addUser(eq(dummyAuthUser));
			verify(userService, times(1)).addUser(eq(dummyReceiverUser));
			verifyNoMoreInteractions(userService);
			verify(moneyTransactionRepository, times(1)).save(eq(expectedTransaction));
			verifyNoMoreInteractions(moneyTransactionRepository);
		}

		@Test
		@DisplayName("Should throw exception when transaction amount is negative")
		void givenNegativeAmountTransaction_whenCreateMoneyTransaction_thenThrowMoneyTransactionNegativeAmountException() {
			dummyTransactionDTO.setAmount(-25.75);

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyAuthUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(dummyReceiverUser));

			assertThatThrownBy(() -> moneyTransactionService.createMoneyTransaction(dummyTransactionDTO))
					.isInstanceOf(MoneyTransactionNegativeAmountException.class)
					.hasMessage("Money transaction amount cannot be negative.");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(dummyTransactionDTO.getReceiverEmail()));
			verifyNoMoreInteractions(userService);
			verifyNoInteractions(moneyTransactionRepository);
		}

		@Test
		@DisplayName("Should throw exception when transaction amount is below minimum amount")
		void givenBelowMinimumAmountTransaction_whenCreateMoneyTransaction_thenThrowMoneyTransactionBelowMinimumAmountException() {
			dummyTransactionDTO.setAmount(0.5);

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyAuthUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(dummyReceiverUser));

			assertThatThrownBy(() -> moneyTransactionService.createMoneyTransaction(dummyTransactionDTO))
					.isInstanceOf(MoneyTransactionBelowMinimumAmountException.class)
					.hasMessage("Money transaction amount cannot be below minimum amount (minimum: 1.0).");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(dummyTransactionDTO.getReceiverEmail()));
			verifyNoMoreInteractions(userService);
			verifyNoInteractions(moneyTransactionRepository);
		}

		@Test
		@DisplayName("Should throw exception when transaction amount exceeds sender balance")
		void givenExceedsSenderBalanceTransaction_whenCreateMoneyTransaction_thenThrowMoneyTransactionExceedsSenderBalanceException() {
			dummyTransactionDTO.setAmount(150.0);

			when(userService.getCurrentUser()).thenReturn(Optional.of(dummyAuthUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(dummyReceiverUser));

			assertThatThrownBy(() -> moneyTransactionService.createMoneyTransaction(dummyTransactionDTO))
					.isInstanceOf(MoneyTransactionExceedsSenderBalanceException.class)
					.hasMessage("Money transaction amount cannot be above sender balance.");

			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(dummyTransactionDTO.getReceiverEmail()));
			verifyNoMoreInteractions(userService);
			verifyNoInteractions(moneyTransactionRepository);
		}
	}
}