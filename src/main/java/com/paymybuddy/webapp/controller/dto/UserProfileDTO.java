package com.paymybuddy.webapp.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
	@NotBlank(message = "Username is mandatory")
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]*$", message = "Username must be alphanumeric and begin with letter.")
	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters.")
	private String username;

	@NotBlank(message = "Email is mandatory.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,63}$", message = "Email should be valid.")
	private String email;

	@NotBlank(message = "Password is mandatory")
	@Pattern(regexp = "^(?! )[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]*(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).*$", message = "Password must contain at least one letter, one number, one special character and never begin with space.")
	@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters long.")
	private String password;
}
