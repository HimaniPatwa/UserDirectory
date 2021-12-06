package com.CISCOproject.exception;

import java.util.UUID;

public class UserNotFoundException extends Exception {
	private Long userId;
	public UserNotFoundException(UUID userId) {
	        super(String.format("User is not found with id : " + userId));
	        }
}


