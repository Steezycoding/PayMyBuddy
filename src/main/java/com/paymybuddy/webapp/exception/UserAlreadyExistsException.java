package com.paymybuddy.webapp.exception;

public class UserAlreadyExistsException extends RegistrationException {
	public UserAlreadyExistsException(String email) {
		super(String.format("User with email '%s' already exists.", email.toLowerCase()));
	}
}
