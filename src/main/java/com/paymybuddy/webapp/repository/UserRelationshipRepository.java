package com.paymybuddy.webapp.repository;

import com.paymybuddy.webapp.model.User;
import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.model.UserRelationshipId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRelationshipRepository extends CrudRepository<UserRelationship, UserRelationshipId> {
	Optional<List<UserRelationship>> findByUserId(User userId);
}
