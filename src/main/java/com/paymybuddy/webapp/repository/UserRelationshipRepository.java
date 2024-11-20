package com.paymybuddy.webapp.repository;

import com.paymybuddy.webapp.model.UserRelationship;
import com.paymybuddy.webapp.model.UserRelationshipId;
import org.springframework.data.repository.CrudRepository;

public interface UserRelationshipRepository extends CrudRepository<UserRelationship, UserRelationshipId> {
}
