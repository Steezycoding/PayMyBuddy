package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.EmailDTO;
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
@RequestMapping("/relationships")
public class RelationshipController {
	private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	@GetMapping
	public String relationshipsPage(Model model) {
		model.addAttribute("title", "Relationships");
		model.addAttribute("view", "relationships");
		model.addAttribute("emailDTO", new EmailDTO());
		return "main-template";
	}

	@PostMapping
	public String addRelation(@Valid @ModelAttribute("emailDTO") EmailDTO emailDTO, BindingResult result, Model model) {
		if (result.hasErrors()) {
			logger.info("User Relation email has errors {}", result.getAllErrors());

			model.addAttribute("title", "Relationships");
			model.addAttribute("view", "relationships");
			return "main-template";
		}
		return "redirect:/relationships";
	}
}
