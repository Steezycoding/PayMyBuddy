package com.paymybuddy.webapp.repository;

import com.paymybuddy.webapp.model.MoneyTransaction;
import com.paymybuddy.webapp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyTransactionRepository extends CrudRepository<MoneyTransaction, Long> {
	List<MoneyTransaction> findBySenderId(User sender);
}
