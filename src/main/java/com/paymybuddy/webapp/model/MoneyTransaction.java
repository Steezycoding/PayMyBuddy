package com.paymybuddy.webapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class MoneyTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "sender", referencedColumnName = "id", nullable = false)
	private User senderId;

	@ManyToOne
	@JoinColumn(name = "receiver", referencedColumnName = "id", nullable = false)
	private User receiverId;
}
