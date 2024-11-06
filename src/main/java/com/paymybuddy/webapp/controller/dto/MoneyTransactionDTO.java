package com.paymybuddy.webapp.controller.dto;

import com.paymybuddy.webapp.model.MoneyTransaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransactionDTO {
	@NotEmpty(message = "Receiver email cannot be empty")
	private String receiverEmail;

	@NotEmpty(message = "Receiver username cannot be empty")
	private String receiverUsername;

	@NotNull
	@Min(value = 1, message = "Minimal amount is 1")
	private Double amount;

	private String description;

	public MoneyTransaction toMoneyTransaction() {
		return MoneyTransaction.builder()
				.amount(this.amount)
				.description(this.description)
				.build();
	}
}
