package com.paymybuddy.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/money-transactions")
public class MoneyTransactionController {

	public MoneyTransactionController() {}

	@GetMapping
	public String moneyTransactionPage(Model model) {
		model.addAttribute("title", "Transactions");
		model.addAttribute("view", "money-transactions");
		return "main-template";
	}
}
