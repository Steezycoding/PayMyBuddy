package com.paymybuddy.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relationships")
public class RelationshipController {
	@GetMapping
	public String relationshipsPage(Model model) {
		model.addAttribute("title", "Relationships");
		model.addAttribute("view", "relationships");
		return "main-template";
	}
}
