package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.UserProfileDTO;
import com.paymybuddy.webapp.exception.UserAlreadyExistsException;
import com.paymybuddy.webapp.service.UserProfileService;
import com.paymybuddy.webapp.utils.Alert;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

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

	@PostMapping
	public String updateProfile(@Valid @ModelAttribute UserProfileDTO user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			logger.info("Profile update has errors {}", result.getAllErrors());

			model.addAttribute("title", "Profile");
			model.addAttribute("view", "profile");
			model.addAttribute("user", userProfileService.getUserProfile());

			return "main-template";
		} else {
			Alert alert = Alert.builder()
					.type(Alert.AlertType.SUCCESS)
					.message("Profile updated successfully")
					.build();

			redirectAttributes.addFlashAttribute("alert", alert);

			userProfileService.updateUser(user);

			return "redirect:/profile";
		}
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public String handleUserAlreadyExistsException(UserAlreadyExistsException e, RedirectAttributes redirectAttributes) {
		Alert alert = Alert.builder()
				.type(Alert.AlertType.DANGER)
				.message("This email is already used by another user")
				.build();

		redirectAttributes.addFlashAttribute("alert", alert);

		return "redirect:/profile";
	}
}
