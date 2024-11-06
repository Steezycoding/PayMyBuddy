package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import com.paymybuddy.webapp.exception.MoneyTransactionException;
import com.paymybuddy.webapp.exception.UserContextNotFoundException;
import com.paymybuddy.webapp.exception.UserNotFoundException;
import com.paymybuddy.webapp.model.MoneyTransaction;
import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.repository.MoneyTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MoneyTransactionService {

	private final MoneyTransactionRepository moneyTransactionRepository;

	private final UserRelationshipService userRelationshipService;

	private final UserService userService;

	public MoneyTransactionService(MoneyTransactionRepository moneyTransactionRepository, UserRelationshipService userRelationshipService, UserService userService) {
		this.moneyTransactionRepository = moneyTransactionRepository;
		this.userRelationshipService = userRelationshipService;
		this.userService = userService;
	}

	/**
	 * Get authenticated user relationships with sanitized data.
	 *
	 * @return the relationships list
	 */
	public List<UserContactDTO> getUserRelationships() {
		return userRelationshipService.getRelationships()
				.stream()
				.map(user -> new UserContactDTO().fromUser(user))
				.collect(Collectors.toList());
	}

	/**
	 * Create a money transaction between the authenticated user and another user.
	 *
	 * @throws UserContextNotFoundException if the authenticated user is not found
	 * @throws UserNotFoundException if the receiver user is not found
	 */
	public void createMoneyTransaction(MoneyTransactionDTO transactionDTO) {
		User sender = userService.getCurrentUser()
				.orElseThrow(() -> new UserContextNotFoundException());

		User receiver = userService.getUserByEmail(transactionDTO.getReceiverEmail())
				.orElseThrow(() -> new UserNotFoundException(transactionDTO.getReceiverEmail()));

		MoneyTransaction transaction = handleTransaction(sender, receiver, transactionDTO);
		moneyTransactionRepository.save(transaction);
	}

	/**
	 * Handles the money transaction between the sender and the receiver.
	 *
	 * @return the money transaction entity
	 *
	 * @throws MoneyTransactionException if transaction construction fails
	 */
	private MoneyTransaction handleTransaction(User sender, User receiver, MoneyTransactionDTO transactionDTO) {
		try {
			sender.setBalance((float) (sender.getBalance() - transactionDTO.getAmount()));
			userService.addUser(sender);

			receiver.setBalance((float) (receiver.getBalance() + transactionDTO.getAmount()));
			userService.addUser(receiver);

			MoneyTransaction transaction = transactionDTO.toMoneyTransaction();
			transaction.setSenderId(sender.getId());
			transaction.setReceiverId(receiver.getId());

			return transaction;
		} catch (Exception e) {
			throw new MoneyTransactionException(e.getMessage());
		}
	}
}
