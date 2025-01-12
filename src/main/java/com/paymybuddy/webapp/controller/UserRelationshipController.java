package com.paymybuddy.webapp.controller;

import com.paymybuddy.webapp.controller.dto.EmailDTO;
import com.paymybuddy.webapp.exception.UserException;
import com.paymybuddy.webapp.exception.UserRelationshipException;
import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.service.UserRelationshipService;
import com.paymybuddy.webapp.utils.Alert;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/relationships")
public class UserRelationshipController {
	private final Logger logger = LoggerFactory.getLogger(RegisterController.class);

	private final UserRelationshipService userRelationshipService;

	public UserRelationshipController(UserRelationshipService userRelationshipService) {
		this.userRelationshipService = userRelationshipService;
	}

	@Operation(summary = "Get the relationships page", description = "Returns the relationships page")
	@ApiResponse(responseCode = "200", description = "Relationships page loaded successfully")
	@GetMapping
	public String relationshipsPage(Model model) {
		model.addAttribute("title", "Relationships");
		model.addAttribute("view", "relationships");
		model.addAttribute("emailDTO", new EmailDTO());
		return "main-template";
	}

	@Operation(summary = "Add a new user relation",
			description = "Adds a new user relation with the provided email",
			requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data",
					schema = @Schema(implementation = EmailDTO.class))))
	@ApiResponse(responseCode = "302", description = "User relation added successfully")
	@PostMapping
	public String addRelation(@Valid @ModelAttribute("emailDTO") EmailDTO emailDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			logger.info("User Relation email has errors {}", result.getAllErrors());

			model.addAttribute("title", "Relationships");
			model.addAttribute("view", "relationships");
			return "main-template";
		} else {
			Alert.AlertBuilder alert = Alert.builder();
			try {
				UserRelationship relation = userRelationshipService.addRelationship(emailDTO.getEmail());

				alert.type(Alert.AlertType.SUCCESS).message("Relation " + relation.getRelationUserId().getEmail() + " added successfully");
			} catch (UserException e) {
				logger.warn("Relationship aborted: {}", e.getMessage());

				alert.type(Alert.AlertType.DANGER).message(e.getMessage());
			} catch (UserRelationshipException e) {
				logger.warn("Relationship aborted: {}", e.getMessage());

				alert.type(Alert.AlertType.WARNING).message(e.getMessage());
			}
			redirectAttributes.addFlashAttribute("alert", alert.build());
			return "redirect:/relationships";
		}
	}
}
