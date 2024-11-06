package com.paymybuddy.webapp.exception;

public class MoneyTransactionException extends RuntimeException {
	public MoneyTransactionException(String message){
		super(message);
	}
}
