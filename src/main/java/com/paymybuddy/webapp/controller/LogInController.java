package com.paymybuddy.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LogInController {
	@GetMapping
	public String loginPage(Model model) {
		model.addAttribute("title", "Login");
		model.addAttribute("view", "login");
		return "main-template";
	}
}
