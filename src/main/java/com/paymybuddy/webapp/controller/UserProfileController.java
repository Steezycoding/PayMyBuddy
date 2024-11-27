package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.service.UserProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	private final UserProfileService userProfileService;

	public UserProfileController(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@GetMapping
	public String profilePage(Model model) {
		model.addAttribute("title", "Profile");
		model.addAttribute("view", "profile");
		model.addAttribute("user", userProfileService.getUserProfile());

		return "main-template";
	}
}
