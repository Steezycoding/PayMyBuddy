package com.paymybuddy.webapp.exception;

import com.paymybuddy.webapp.model.UserRelationshipId;

public class UserRelationshipAlreadyExistsException extends UserRelationshipException {
	public UserRelationshipAlreadyExistsException(UserRelationshipId relationship) {
		super(String.format("Relationship between %s and %s already exists.",
				relationship.getUserId().getEmail(),
				relationship.getRelationUserId().getEmail()
		));
	}
}
