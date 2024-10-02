package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.UserRegistrationDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@GetMapping
	public String registerPage(Model model) {
		model.addAttribute("title", "Register");
		model.addAttribute("view", "register");
		model.addAttribute("user", new UserRegistrationDTO());
		return "main-template";
	}
}
