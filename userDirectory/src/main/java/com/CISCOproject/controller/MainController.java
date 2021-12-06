package com.CISCOproject.controller;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.CISCOproject.exception.PhoneNotFoundException;
import com.CISCOproject.exception.UserNotFoundException;
import com.CISCOproject.model.Phone;
import com.CISCOproject.model.User;
import com.CISCOproject.services.UserPhoneContorller;

@Controller
@RequestMapping(path = "/cisco")
public class MainController {

	@Autowired
	private UserPhoneContorller controller;
	private static Logger log = Logger.getLogger(UserPhoneContorller.class.getName());

	/***
	 * Add user to a system. This function adds a new user with phone to the
	 * repository/system. It returns the newly created User details with a unique Id
	 * for both User and Phone.
	 * 
	 * @param name
	 * @param password
	 * @param email
	 * @param phonename
	 * @param phonemodel
	 * @param phonenumber
	 * @return
	 */
	@Transactional
	@PostMapping(path = "/adduser")
	public @ResponseBody ResponseEntity<String> addNewUser(@RequestParam String name, @RequestParam String password,
			@RequestParam String email, @RequestParam String phonename, @RequestParam String phonemodel,
			@RequestParam String phonenumber) {
		log.info("Add user API called");
		return controller.addNewUserAndPhone(name, password, email, phonemodel, phonename, phonenumber);
	}

	/***
	 * This function deletes a user from system with given USERID
	 * 
	 * @param userid
	 * @return
	 */
	@DeleteMapping(path = "/delete")
	public @ResponseBody ResponseEntity<String> deleteUser(@RequestParam UUID userid) {
		log.info("Delete user API called with user id " + userid);
		return controller.deleteUser(userid);
	}

	/***
	 * This function gives list of users present in the repository/system
	 * 
	 * @return
	 */

	@GetMapping(path = "/user")
	public @ResponseBody List<User> getAllUsers() {
		log.info("List all users API called");
		return controller.listAllUsers();
	}

	/***
	 * This function adds a new phone to an existing user in the repository/system.
	 * 
	 * @param phonename
	 * @param phonemodel
	 * @param phonenumber
	 * @param userid
	 * @param preferredphonenumber
	 * @return
	 */

	@PostMapping(path = "/addphone")
	public @ResponseBody ResponseEntity<String> addPhone(@RequestParam String phonename,
			@RequestParam String phonemodel, @RequestParam String phonenumber, @RequestParam UUID userid,
			@RequestParam boolean preferredphonenumber) {
		log.info("Add phone with existing user API is called");

		try {

			return controller.addPhoneToExistingUser(phonemodel, phonename, phonenumber, preferredphonenumber, userid);
		} catch (UserNotFoundException e) {
			log.error("User with user id " + userid + " not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

	}

	/***
	 * This function deletes a user's phone number
	 * 
	 * @param phonenumber
	 * @return
	 * @throws PhoneNotFoundException
	 */
	@DeleteMapping(path = "/deletephone")
	public @ResponseBody ResponseEntity<String> deletePhone(@RequestParam String phonenumber)
			throws PhoneNotFoundException {
		log.info("Delete phone API is called for phone number " + phonenumber);
		return controller.deletePhone(phonenumber);
	}

	/***
	 * This function list all the phones for a user
	 * 
	 * @param userid
	 * @return
	 */
	@GetMapping(path = "/listphones")
	public @ResponseBody List<Phone> listPhones(@RequestParam UUID userid) {
		log.info("List phones API is called for user id " + userid);
		return controller.listAUsersPhones(userid);

	}

	/***
	 * This function update the preferred phone number for an existing user in the
	 * repository/system
	 * 
	 * @param userid
	 * @param phonenumber
	 * @return
	 * @throws PhoneNotFoundException
	 * @throws UserNotFoundException
	 */
	@PutMapping(path = "/updatepreferredphonenumber")
	public @ResponseBody ResponseEntity<String> updatePreferredPhoneNumber(@RequestParam UUID userid,
			@RequestParam String phonenumber) throws PhoneNotFoundException, UserNotFoundException {
		log.info("Update preferred phone number API is called with user id " + userid);
		return controller.updateUsersPreferredPhoneNumber(userid, phonenumber);
	}
}
