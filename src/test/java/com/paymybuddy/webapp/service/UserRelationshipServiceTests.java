package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.exception.*;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.model.UserRelationshipId;
import com.paymybuddy.webapp.repository.UserRelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelationshipService Test Suite")
public class UserRelationshipServiceTests {
	@Mock
	private UserService userService;

	@Mock
	private UserRelationshipRepository userRelationshipRepository;

	@InjectMocks
	private UserRelationshipService userRelationshipService;

	@Nested
	@DisplayName("addRelationship() Tests")
	class AddUserRelationshipTests {

		private User currentUser;
		private User relationUser;

		@BeforeEach
		void setUp() {
			currentUser = User.builder()
					.id(1L)
					.username("auth.user")
					.email("auth.user@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();

			relationUser = User.builder()
					.id(2L)
					.username("j.doe")
					.email("j.doe@email.com")
					.password("encodedPassword")
					.role("USER")
					.balance(0f)
					.build();
		}

		@Test
		@DisplayName("Should add new relation if RELATION user exists and relationship NOT exists")
		void givenRelationUserExists_andRelationshipNotExists_whenAddRelationship_thenRelationshipSaved() {
			UserRelationship relationship = UserRelationship.builder()
					.userId(currentUser)
					.relationUserId(relationUser)
					.build();
			when(userService.getCurrentUser()).thenReturn(Optional.of(currentUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(relationUser));
			when(userRelationshipRepository.existsById(any(UserRelationshipId.class))).thenReturn(false);


			userRelationshipService.addRelationship(relationUser.getEmail());

			verify(userService, times(1)).getUserByEmail(eq(relationUser.getEmail()));
			verify(userService, times(1)).getCurrentUser();
			verifyNoMoreInteractions(userService);
			verify(userRelationshipRepository, times(1)).existsById(eq(new UserRelationshipId(currentUser, relationUser)));
			verify(userRelationshipRepository, times(1)).save(eq(relationship));
			verifyNoMoreInteractions(userRelationshipRepository);
		}

		@Test
		@DisplayName("Should NOT add new relation if CURRENT user NOT exists")
		void givenCurrentUserNotExists_whenAddRelationship_thenExceptionRaised() {
			when(userService.getCurrentUser()).thenReturn(Optional.empty());

			UserException exception = assertThrows(UserContextNotFoundException.class, () -> {
				userRelationshipService.addRelationship(relationUser.getEmail());
			});

			String expectedExceptionMessage = "Authenticated user cannot be found.";

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(userService, times(1)).getCurrentUser();
			verifyNoMoreInteractions(userService);
			verifyNoInteractions(userRelationshipRepository);
		}

		@Test
		@DisplayName("Should NOT add new relation if RELATION user NOT exists")
		void givenRelationUserNotExists_whenAddRelationship_thenExceptionRaised() {
			String nonExistingEmail = "email@not.exists";

			when(userService.getCurrentUser()).thenReturn(Optional.of(currentUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.empty());

			UserException exception = assertThrows(UserNotFoundException.class, () -> {
				userRelationshipService.addRelationship(nonExistingEmail);
			});

			String expectedExceptionMessage = String.format("User with email '%s' doesn't exist.", nonExistingEmail);

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(nonExistingEmail));
			verifyNoMoreInteractions(userService);
			verifyNoInteractions(userRelationshipRepository);
		}

		@Test
		@DisplayName("Should NOT add new relation if relationship already exists")
		void givenRelationshipExists_whenAddRelationship_thenExceptionRaised() {
			when(userService.getCurrentUser()).thenReturn(Optional.of(currentUser));
			when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(relationUser));
			when(userRelationshipRepository.existsById(any(UserRelationshipId.class))).thenReturn(true);

			UserRelationshipException exception = assertThrows(UserRelationshipAlreadyExistsException.class, () -> {
				userRelationshipService.addRelationship(relationUser.getEmail());
			});

			String expectedExceptionMessage = String.format(
					"Relationship between %s and %s already exists.",
					currentUser.getEmail(),
					relationUser.getEmail()
			);

			assertThat(exception.getMessage()).isEqualTo(expectedExceptionMessage);
			verify(userService, times(1)).getCurrentUser();
			verify(userService, times(1)).getUserByEmail(eq(relationUser.getEmail()));
			verifyNoMoreInteractions(userService);
			verify(userRelationshipRepository, times(1)).existsById(eq(new UserRelationshipId(currentUser, relationUser)));
			verifyNoMoreInteractions(userRelationshipRepository);
		}

	}
}
