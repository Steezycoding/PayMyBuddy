package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.MoneyTransactionDTO;
import com.paymybuddy.webapp.controller.dto.UserRegistrationDTO;
import com.paymybuddy.webapp.exception.RegistrationException;
import com.paymybuddy.webapp.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {
	private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	private final RegisterService registerService;

	public RegisterController(RegisterService registerService) {
		this.registerService = registerService;
	}

	@Operation(summary = "Get the registration page", description = "Returns the registration page")
	@ApiResponse(responseCode = "200", description = "Registration page loaded successfully")
	@GetMapping
	public String registerPage(Model model) {
		model.addAttribute("title", "Register");
		model.addAttribute("view", "register");
		model.addAttribute("user", new UserRegistrationDTO());
		return "main-template";
	}

	@Operation(summary = "Register a new user",
			description = "Registers a new user with the provided information",
			requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data",
			schema = @Schema(implementation = UserRegistrationDTO.class))))
	@ApiResponse(responseCode = "302", description = "User registered successfully")
	@PostMapping
	public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			logger.info("User Registration form has errors {}", result.getAllErrors());

			model.addAttribute("title", "Register");
			model.addAttribute("view", "register");
			return "main-template";
		} else {
			try {
				registerService.registerUser(user);

				logger.info("Registration succeed: User {} added", user.getEmail().toLowerCase());

				model.addAttribute("title", "Login");
				model.addAttribute("view", "login");
				return "redirect:/login";
			} catch (RegistrationException e) {
				logger.info("Registration aborted: User '{}' already exists", user.getEmail().toLowerCase());

				model.addAttribute("title", "Register");
				model.addAttribute("view", "register");
				result.rejectValue("email", "error.user.email.exists", new Object[]{user.getEmail()}, e.getMessage());
				return "main-template";
			}
		}
	}
}
