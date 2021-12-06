package com.CISCOproject.services;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.CISCOproject.exception.UserNotFoundException;
import com.CISCOproject.model.Phone;
import com.CISCOproject.model.User;

public interface UserPhoneContorller {

	public ResponseEntity<String> addPhoneToExistingUser(String phoneModel, String phoneName, String phoneNumber,
			boolean preferredPhoneNumber, UUID userId) throws UserNotFoundException;

	public ResponseEntity<String> addNewUserAndPhone(String userName, String password, String email, String phoneModel, String phoneName,
			String phoneNumber);

	public ResponseEntity<String> deleteUser(UUID userId);

	public List<User> listAllUsers();

	public ResponseEntity<String> deletePhone(String phoneNumber);

	public List<Phone> listAUsersPhones(UUID userId);

	public ResponseEntity<String> updateUsersPreferredPhoneNumber(UUID userId, String phoneNumber);
}
