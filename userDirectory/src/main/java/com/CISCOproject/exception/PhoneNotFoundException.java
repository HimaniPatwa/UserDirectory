package com.CISCOproject.exception;

public class PhoneNotFoundException extends Exception {
	public PhoneNotFoundException()
	{
		super(String.format("Phone not found" ));
	}

}
