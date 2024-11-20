package com.paymybuddy.webapp.exception;

public class UserNotFoundException extends UserException {
	public UserNotFoundException(String email) {
		super(String.format("User with email '%s' doesn't exist.", email.toLowerCase()));
	}
}
