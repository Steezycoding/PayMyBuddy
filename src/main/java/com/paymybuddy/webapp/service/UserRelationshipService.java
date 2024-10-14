package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.exception.UserNotFoundException;
import com.paymybuddy.webapp.exception.UserRelationshipAlreadyExistsException;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.model.UserRelationshipId;
import com.paymybuddy.webapp.repository.UserRelationshipRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserRelationshipService {
	private UserRelationshipRepository userRelationshipRepository;
	private UserService userService;

	public UserRelationshipService(UserRelationshipRepository userRelationshipRepository, UserService userService) {
		this.userRelationshipRepository = userRelationshipRepository;
		this.userService = userService;
	}

	/***
	 * Creates a relationship between the current (authenticated) user and another existing user.
	 *
	 * @param email the email of the relation to add
	 * @return the user relationship
	 * @throws UserContextNotFoundException if the authenticated user can not be retrieved
	 * @throws UserNotFoundException if the user to add (relation) can not be retrieved
	 * @throws UserRelationshipAlreadyExistsException if the relationship already exists
	 */
	public UserRelationship addRelationship(String email) {
		User currentUser = userService.getCurrentUser()
				.orElseThrow(() -> new UserContextNotFoundException());

		User relationUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new UserNotFoundException(email));

		UserRelationshipId relationshipId = new UserRelationshipId(currentUser, relationUser);

		if (userRelationshipRepository.existsById(relationshipId)) {
			throw new UserRelationshipAlreadyExistsException(relationshipId);
		}

		UserRelationship relationship = UserRelationship.builder()
				.userId(currentUser)
				.relationUserId(relationUser)
				.build();

		return userRelationshipRepository.save(relationship);
	}
}
