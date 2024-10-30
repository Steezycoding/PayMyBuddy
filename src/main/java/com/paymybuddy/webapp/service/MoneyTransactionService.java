package com.paymybuddy.webapp.service;

import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoneyTransactionService {
	private final UserRelationshipService userRelationshipService;

	public MoneyTransactionService(UserRelationshipService userRelationshipService) {
		this.userRelationshipService = userRelationshipService;
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
}
