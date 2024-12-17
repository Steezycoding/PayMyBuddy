package com.paymybuddy.webapp.controller.dto;

import com.paymybuddy.webapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserContactDTO {
	private Long id;

	private String username;

	private String email;

	public UserContactDTO fromUser(User user) {
		return UserContactDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.build();
	}
}
