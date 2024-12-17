package com.paymybuddy.webapp.exception;

import com.paymybuddy.webapp.model.User;

public class UserRelationshipNoRelationException extends UserRelationshipException {
	public UserRelationshipNoRelationException(User currentUser) {
		super(String.format("No contacts found for the current user %s", currentUser));
	}
}
