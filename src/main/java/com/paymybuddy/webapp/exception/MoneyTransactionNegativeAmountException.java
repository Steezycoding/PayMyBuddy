package com.paymybuddy.webapp.exception;

public class MoneyTransactionNegativeAmountException extends MoneyTransactionException {
	public MoneyTransactionNegativeAmountException() {
		super("Money transaction amount cannot be negative.");
	}
}
