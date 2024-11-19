package com.paymybuddy.webapp.exception;

public class MoneyTransactionExceedsSenderBalanceException extends MoneyTransactionException {
	public MoneyTransactionExceedsSenderBalanceException() {
		super("Money transaction amount cannot be above sender balance.");
	}
}
