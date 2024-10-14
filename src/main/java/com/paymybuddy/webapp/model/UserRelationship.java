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
@Table(name = "user_relationships")
@IdClass(UserRelationshipId.class)
public class UserRelationship {
	@Id
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private User userId;

	@Id
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "relation_user_id",  referencedColumnName = "id", nullable = false)
	private User relationUserId;
}
