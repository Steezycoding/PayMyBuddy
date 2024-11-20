package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	@GetMapping
	public String profilePage(Model model) {
		model.addAttribute("title", "Profile");
		model.addAttribute("view", "profile");
		model.addAttribute("user", new UserProfileDTO());

		return "main-template";
	}
}
