package com.paymybuddy.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LogInController {
	@Operation(summary = "Get the login page", description = "Returns the login page")
	@ApiResponse(responseCode = "200", description = "Login page loaded successfully")
	@GetMapping
	public String loginPage(Model model) {
		model.addAttribute("title", "Login");
		model.addAttribute("view", "login");
		return "main-template";
	}
}
