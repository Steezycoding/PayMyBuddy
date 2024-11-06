package com.paymybuddy.webapp.repository;

import com.paymybuddy.webapp.model.MoneyTransaction;
import org.springframework.data.repository.CrudRepository;

public interface MoneyTransactionRepository extends CrudRepository<MoneyTransaction, Long> {
}
