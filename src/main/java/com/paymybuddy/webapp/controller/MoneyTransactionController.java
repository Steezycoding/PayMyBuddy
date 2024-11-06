package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserContactDTO;
import com.paymybuddy.webapp.exception.UserRelationshipNoRelationException;
import com.paymybuddy.webapp.service.MoneyTransactionService;
import com.paymybuddy.webapp.utils.Alert;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/money-transactions")
public class MoneyTransactionController {
	private final Logger logger = LoggerFactory.getLogger(MoneyTransactionController.class);

	private MoneyTransactionService moneyTransactionService;

	public MoneyTransactionController(MoneyTransactionService moneyTransactionService) {
		this.moneyTransactionService = moneyTransactionService;
	}

	@GetMapping
	public String moneyTransactionPage(Model model) {
		model.addAttribute("title", "Transactions");
		model.addAttribute("view", "money-transactions");
		model.addAttribute("transaction", new MoneyTransactionDTO());

		List<UserContactDTO> contacts = moneyTransactionService.getUserRelationships();
		model.addAttribute("contacts", contacts);

		return "main-template";
	}

	@PostMapping
	public String createMoneyTransaction(@Valid @ModelAttribute("transaction") MoneyTransactionDTO transaction,
										 BindingResult result,
										 RedirectAttributes redirectAttributes,
										 Model model) {
		if (result.hasErrors()) {
			logger.error("Money Transaction form has errors {}", result.getAllErrors());

			model.addAttribute("title", "Transactions");
			model.addAttribute("view", "money-transactions");
			return "main-template";
		}

		moneyTransactionService.createMoneyTransaction(transaction);

		Alert alert = Alert.builder()
				.type(Alert.AlertType.SUCCESS)
				.message(String.format("The payment was successfully sent to %s.", transaction.getReceiverUsername()))
				.build();

		redirectAttributes.addFlashAttribute("alert", alert);

		return "redirect:/money-transactions";
	}

	@ExceptionHandler(UserRelationshipNoRelationException.class)
	public String handleNoRelationException(UserRelationshipNoRelationException e, RedirectAttributes redirectAttributes) {
		logger.warn(e.getMessage());

		Alert alert = Alert.builder()
				.type(Alert.AlertType.WARNING)
				.message("You have no contact available for transactions. Please add a contact to initiate a payment.")
				.build();

		redirectAttributes.addFlashAttribute(alert);

		return "redirect:/relationships";
	}
}
