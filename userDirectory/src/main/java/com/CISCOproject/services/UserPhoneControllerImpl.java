package com.CISCOproject.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CISCOproject.exception.UserNotFoundException;
import com.CISCOproject.model.Phone;
import com.CISCOproject.model.User;
import com.CISCOproject.repository.PhoneRepository;
import com.CISCOproject.repository.UserRepository;

@Service("UserPhoneController")
public class UserPhoneControllerImpl implements UserPhoneContorller {

	final private PhoneRepository phoneRepository;

	final private UserRepository userRepository;

	private static Logger log = Logger.getLogger(UserPhoneControllerImpl.class.getName());

	public UserPhoneControllerImpl(@Autowired PhoneRepository phoneRepository,
			@Autowired UserRepository userRepository) {
		this.phoneRepository = phoneRepository;
		this.userRepository = userRepository;
	}

	/***
	 * This method add a user and its phone to the repository and checks for
	 * duplicate phone number
	 */
	@Transactional
	@Override
	public ResponseEntity<String> addNewUserAndPhone(final String userName, final String password, final String email,
			final String phoneModel, final String phoneName, final String phoneNumber) {
		// add user first and then add phone.
		User user = User.builder().userName(userName).password(password).emailId(email).build();
		user = userRepository.save(user);
		log.info(user.toString() + " Saved");
		Phone phone = Phone.builder().isPreferredPhoneNumber(true).phoneModel(phoneModel).phoneName(phoneName)
				.phoneNumber(phoneNumber).user(user).build();
		return savePhone(phone);
	}

	private ResponseEntity<String> savePhone(Phone phone) {
		try {

			phone = phoneRepository.save(phone);
			log.info(phone.toString() + " Saved");
			return new ResponseEntity<String>(phone.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Save Phone record Operation failed due to ", e);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional
	@Override
	public ResponseEntity<String> addPhoneToExistingUser(final String phoneModel, final String phoneName,
			final String phoneNumber, final boolean preferredPhoneNumber, final UUID userId)
			throws UserNotFoundException {

		// verify if user exists and then add new phone for that user.
		final User user = getUser(userId);
		log.info(user.toString() + " Found in DB");
		final Phone newPhone = Phone.builder().phoneModel(phoneModel).phoneName(phoneName)
				.isPreferredPhoneNumber(preferredPhoneNumber).phoneNumber(phoneNumber).user(user).build();
		log.info("Check if already a preferred phone number present for the user " + user.getUserId());
		// update if already a preferred phone in db
		updatePreferredPhoneNumber(preferredPhoneNumber, userId);
		return savePhone(newPhone);
	}

	private void updatePreferredPhoneNumber(final boolean preferredphoneNumber, final UUID userId) {

		if (preferredphoneNumber) {
			final Optional<Phone> userPhone = phoneRepository.findByUserIdAndPreferredNumber(userId.toString());
			if (userPhone.isPresent()) {
				log.info("Already a preferred phone number is present for " + userPhone.get().getUser().getUserId());
				log.info("Setting preferred phone number to be false");
				userPhone.get().setPreferredPhoneNumber(false);
				phoneRepository.save(userPhone.get());
			}
		}
	}

	private User getUser(final UUID userId) throws UserNotFoundException {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	@Override
	public ResponseEntity<String> deleteUser(final UUID userId) {
		try {
			userRepository.deleteById(userId);
			log.info("User delete with id " + userId);
			return new ResponseEntity<String>(userId + " is deleted", HttpStatus.OK);
		} catch (EmptyResultDataAccessException e) {
			log.error("User not found with id " + userId);
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@Transactional
	@Override
	public List<User> listAllUsers() {
		final List<User> users = userRepository.findAll();
		log.info("All users listed");
		for (User user : users) {
			final Optional<Phone> preferredPhone = phoneRepository.findByUserIdAndPreferredNumber(user.getUserId().toString());
			if (preferredPhone.isPresent()) {
				user.setPreferredPhoneNumber(preferredPhone.get().getPhoneNumber());
			} else {
				user.setPreferredPhoneNumber("Preffered number not set.");
			}
		}
		return users;
	}

	@Transactional
	@Override
	public ResponseEntity<String> deletePhone(final String phoneNumber) {

		if (phoneRepository.findByPhoneNumber(phoneNumber).isPresent()) {
			phoneRepository.deleteByPhoneNumber(phoneNumber);
			log.info("Phone record with phone number " + phoneNumber + " is deleted");
			return new ResponseEntity<String>(phoneNumber + " is deleted", HttpStatus.OK);
		} else {
			log.error("Phone number not found ");
			return new ResponseEntity<String>("Phone with " + phoneNumber + " not found", HttpStatus.NOT_FOUND);

		}
	} 

	@Transactional
	@Override
	public List<Phone> listAUsersPhones(final UUID userId) {
		log.info("Phone record for user id " + userId + " is generated");
		List<Phone> phones = phoneRepository.findByUserId(userId.toString());
		
		return phones;
	}

	@Transactional
	@Override
	public ResponseEntity<String> updateUsersPreferredPhoneNumber(final UUID userId, final String phoneNumber) {

		final Optional<Phone> userPhone = phoneRepository.findByUserIdAndPreferredNumber(userId.toString());
		log.info(userPhone.toString());
		if (userPhone.isPresent()) {
			log.info("User with user id  "+  userId + "is present in table phone");
			userPhone.get().setPhoneNumber(phoneNumber);
			phoneRepository.save(userPhone.get());
			log.info("User " + userId + "preferred phone number is updated");
			return new ResponseEntity<String>(phoneNumber + " is updated for user " + userId, HttpStatus.OK);
		} else
		{
			log.error("Preferred Phone record not found for user "+ userId);
			return new ResponseEntity<String>("No preferred phone number found for " + userId, HttpStatus.NOT_FOUND);
		}
	}

}
