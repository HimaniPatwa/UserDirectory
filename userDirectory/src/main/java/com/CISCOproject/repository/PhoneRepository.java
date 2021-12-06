package com.CISCOproject.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.CISCOproject.model.Phone;
import com.CISCOproject.model.User;


	// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
	// CRUD refers Create, Read, Update, Delete

	public interface PhoneRepository extends CrudRepository<Phone, UUID> {
		List<Phone> findByUser(User user, Sort sort);

		Optional<Phone> findByPhoneNumber(String phonenumber);
		Optional<Phone> findByPhoneId(UUID phoneid);
		@Query(value="Select * from phone where userid= :userid AND preferredphonenumber=1", nativeQuery=true)
		Optional<Phone> findByUserIdAndPreferredNumber(@Param("userid") String userid);
		@Query(value="Select * from phone where userid= :userid", nativeQuery=true)
		List<Phone> findByUserId(@Param("userid") String userid);
		void deleteByPhoneNumber(String phonenumber);
	}

