package com.paymybuddy.webapp.exception;

public class UserContextNotFoundException extends UserException {
	public UserContextNotFoundException() {
		super("Authenticated user cannot be found.");
	}
}