package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import com.paymybuddy.webapp.model.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MoneyTransactionService Test Suite")
class MoneyTransactionServiceTests {
	@Mock
	private UserRelationshipService userRelationshipService;

	@InjectMocks
	private MoneyTransactionService moneyTransactionService;

	@Nested
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
}