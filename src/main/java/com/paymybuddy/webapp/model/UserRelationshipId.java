package com.paymybuddy.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationshipId implements Serializable {
	private User userId;

	private User relationUserId;
}
