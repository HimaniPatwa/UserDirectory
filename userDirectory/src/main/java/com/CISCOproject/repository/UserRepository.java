package com.CISCOproject.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.CISCOproject.model.User;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, UUID> {
	
	List<User> findAll();
	Optional<User> findById(UUID userId);
	void deleteById(UUID userId);

}
