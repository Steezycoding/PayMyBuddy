package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserContactDTO;
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
					.senderId(dummyAuthUser.getId())
					.receiverId(dummyReceiverUser.getId())
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
	}
}