package com.paymybuddy.webapp.exception;

public class MoneyTransactionBelowMinimumAmountException extends MoneyTransactionException{
	public MoneyTransactionBelowMinimumAmountException(Double amount) {
		super(String.format("Money transaction amount cannot be below minimum amount (minimum: %s).", amount));
	}
}
