package com.CISCOproject.serivces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.CISCOproject.exception.UserNotFoundException;
import com.CISCOproject.model.Phone;
import com.CISCOproject.model.User;
import com.CISCOproject.repository.PhoneRepository;
import com.CISCOproject.repository.UserRepository;
import com.CISCOproject.services.UserPhoneContorller;
import com.CISCOproject.services.UserPhoneControllerImpl;

public class UserPhoneControllerImplTest {

	final static String userName = "test";
	final static String email = "e@g.com";
	final static String pass = "65662";
	final static String phoneNum = "123243";
	final static String phoneModel = "Andriod";
	final static String phoneName = "test phone";

	@Mock
	private PhoneRepository mockPhoneRepository;
	@Mock
	private UserRepository mockUserRepository;

	UserPhoneContorller classUnderTest;

	@AfterMethod
	public void afterMethod() {
		Mockito.reset(mockPhoneRepository);
		Mockito.reset(mockUserRepository);
	}

	@BeforeClass
	public void setup() {
		MockitoAnnotations.initMocks(this);
		classUnderTest = new UserPhoneControllerImpl(mockPhoneRepository, mockUserRepository);
	}

	@DataProvider(name = "testAddNewUserAndPhonerData")
	public Object[][] getUserData() {
		return new Object[][] {
				// isPhoneAlreadyExist
				{ false }, { true } };
	}

	@Test(dataProvider = "testAddNewUserAndPhonerData")
	public void testAddNewUserAndPhone(final boolean isPhoneAlreadyExist) {
		// mock
		final Phone phone = getPhoneWithUserData();
		final User user = phone.getUser();
		when(mockUserRepository.save(any(User.class))).thenReturn(user);
		final RuntimeException e = new RuntimeException("PhoneNumber already exists.");
		if (!isPhoneAlreadyExist) {
			when(mockPhoneRepository.save(any(Phone.class))).thenReturn(phone);
		} else {
			when(mockPhoneRepository.save(any(Phone.class))).thenThrow(e);
		}

		// call
		final ResponseEntity<String> actualData = classUnderTest.addNewUserAndPhone(userName, pass, email, phoneModel,
				phoneName, phoneNum);

		// verify
		final ResponseEntity<String> expectedData;
		if (isPhoneAlreadyExist) {
			expectedData = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} else {
			expectedData = new ResponseEntity<String>(phone.toString(), HttpStatus.OK);
		}
		Assert.assertEquals(actualData, expectedData);
		user.setUserId(null);
		phone.setPhoneId(null);
		verify(mockPhoneRepository).save(eq(phone));

		verify(mockUserRepository).save(eq(user));
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);

	}

	@DataProvider(name = "testAddPhoneToExistingUserData")
	public Object[][] getData() {
		return new Object[][] {
				// isPreferredPhoneNumber, isPrevoiusDataPresent
				{ false, false }, { true, false }, { true, true } };
	}

	@Test(dataProvider = "testAddPhoneToExistingUserData")
	public void testAddPhoneToExistingUser(final boolean isPreferredPhoneNumber, final boolean isPrevoiusDataPresent)
			throws UserNotFoundException {
		// mock
		final Phone phone = getPhoneWithUserData();
		phone.setPreferredPhoneNumber(isPreferredPhoneNumber);
		final Optional<User> user = Optional.of(phone.getUser());

		when(mockUserRepository.findById(user.get().getUserId())).thenReturn(user);
		when(mockPhoneRepository.save(any(Phone.class))).thenReturn(phone);

		// mock if previous preffered number is already present in db.
		Phone previousPreferredPhone = null;
		if (isPreferredPhoneNumber) {
			if (isPrevoiusDataPresent) {
				previousPreferredPhone = Phone.builder().phoneNumber("2434324").isPreferredPhoneNumber(true)
						.phoneModel(phoneModel).phoneName(phoneName).user(user.get()).build();
				when(mockPhoneRepository.findByUserIdAndPreferredNumber(user.get().getUserId().toString()))
						.thenReturn(Optional.of(previousPreferredPhone));
			} else {
				when(mockPhoneRepository.findByUserIdAndPreferredNumber(user.get().getUserId().toString()))
						.thenReturn(Optional.ofNullable(null));
			}

		}

		// call
		final ResponseEntity<String> actualData = classUnderTest.addPhoneToExistingUser(phoneModel, phoneName, phoneNum,
				isPreferredPhoneNumber, user.get().getUserId());

		// verify
		final ResponseEntity<String> expectedData = new ResponseEntity<String>(phone.toString(), HttpStatus.OK);
		Assert.assertEquals(actualData, expectedData);
		phone.setPhoneId(null);
		verify(mockPhoneRepository).save(eq(phone));
		verify(mockUserRepository).findById(user.get().getUserId());
		if (isPreferredPhoneNumber) {
			verify(mockPhoneRepository).findByUserIdAndPreferredNumber(user.get().getUserId().toString());
			if (isPrevoiusDataPresent) {
				// previous number flag should be false.
				previousPreferredPhone.setPreferredPhoneNumber(false);
				verify(mockPhoneRepository).save(previousPreferredPhone);
			}
		}

		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	private Phone getPhoneWithUserData() {

		final UUID userUUID = UUID.randomUUID();
		final UUID phoneUUID = UUID.randomUUID();
		final User user = User.builder().userId(userUUID).userName(userName).emailId(email).password(pass).build();

		final Phone phone = Phone.builder().phoneId(phoneUUID).phoneModel(phoneModel).phoneName(phoneName)
				.phoneNumber(phoneNum).user(user).isPreferredPhoneNumber(true).build();
		return phone;
		
	}

	@Test
	public void testDeleteUser() throws UserNotFoundException {
		final User user = getPhoneWithUserData().getUser();

		// call
		classUnderTest.deleteUser(user.getUserId());

		// verify
		verify(mockUserRepository).deleteById(eq(user.getUserId()));
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	public void testDeleteUser_UserNotFound() throws UserNotFoundException {
		// mock
		
		doThrow(new EmptyResultDataAccessException("Not Found", 1)).when(mockUserRepository).deleteById(any(UUID.class));
		// call
		final UUID userUUID = UUID.randomUUID();
		
		final ResponseEntity<String> actual = classUnderTest.deleteUser(userUUID);

		// verify
		verify(mockUserRepository).deleteById(eq(userUUID));
		final ResponseEntity<String> expected = new ResponseEntity<String>("Not Found", HttpStatus.NOT_FOUND);
		assertEquals(actual, expected);
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	public void testListUser() {
		
		final UUID firstUserUUID = UUID.randomUUID();
		final UUID secondUserUUID = UUID.randomUUID();
		final User firstUser = User.builder().userId(firstUserUUID).userName(userName).emailId(email).password(pass).build();
		final User secondUser = User.builder().userId(secondUserUUID).userName("John").emailId("john@example.com").password("pass")
				.build();

		final Phone firstUserPhone = getPhoneWithUserData();
		final Phone temp = null;
		final List<User> expectedList = new ArrayList<>();
		expectedList.add(firstUser);
		expectedList.add(secondUser);

		when(mockUserRepository.findAll()).thenReturn(expectedList);

		when(mockPhoneRepository.findByUserIdAndPreferredNumber(firstUser.getUserId().toString()))
				.thenReturn(Optional.of(firstUserPhone));

		when(mockPhoneRepository.findByUserIdAndPreferredNumber(secondUser.getUserId().toString()))
				.thenReturn(Optional.ofNullable(temp));
		// call
		final List<User> actualList = classUnderTest.listAllUsers();

		// verify
		firstUser.setPreferredPhoneNumber(firstUserPhone.getPhoneNumber());
		secondUser.setPreferredPhoneNumber("Preffered number not set.");
		Assert.assertEquals(actualList, expectedList);
		verify(mockUserRepository).findAll();
		verify(mockPhoneRepository).findByUserIdAndPreferredNumber(eq(firstUser.getUserId().toString()));
		verify(mockPhoneRepository).findByUserIdAndPreferredNumber(eq(secondUser.getUserId().toString()));
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	public void testDeletePhone() throws UserNotFoundException {
		final Phone phone = getPhoneWithUserData();

		// call
		when(mockPhoneRepository.findByPhoneNumber(phone.getPhoneNumber())).thenReturn(Optional.of(phone));
		classUnderTest.deletePhone(phone.getPhoneNumber());

		// verify
		verify(mockPhoneRepository).findByPhoneNumber(phoneNum);
		verify(mockPhoneRepository).deleteByPhoneNumber(phone.getPhoneNumber());
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	public void testDeletePhone_PhoneNotFound() throws UserNotFoundException {
		// mock
		when(mockPhoneRepository.findByPhoneNumber(anyString())).thenReturn(Optional.ofNullable(null));
		final String phoneNum = "31212";
		// call
		final ResponseEntity<String> actual = classUnderTest.deletePhone(phoneNum);

		// verify
		verify(mockPhoneRepository).findByPhoneNumber(phoneNum);
		final ResponseEntity<String> expected = new ResponseEntity<String>("Phone with " + phoneNum + " not found",
				HttpStatus.NOT_FOUND);
		assertEquals(actual, expected);
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	public void testlistAUsersPhones() {
		final List<Phone> phone = List.of(getPhoneWithUserData());
		final UUID userUUID = UUID.randomUUID();
		// mock
		when(mockPhoneRepository.findByUserId(anyString())).thenReturn(phone);
		
		// call
		final List<Phone> actual = classUnderTest.listAUsersPhones(userUUID);

		// verify
		verify(mockPhoneRepository).findByUserId(userUUID.toString());
		assertEquals(actual, phone);
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);

		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

	@DataProvider(name = "testupdateUsersPreferredPhoneNumberData")
	public Object[][] getUpdateUsersPreferredPhoneNumberData() {
		return new Object[][] { { Optional.of(getPhoneWithUserData()) }, { Optional.ofNullable(null) } };
	}

	@Test(dataProvider = "testupdateUsersPreferredPhoneNumberData")
	public void testupdateUsersPreferredPhoneNumber(final Optional<Phone> phone) {
		// mock
		when(mockPhoneRepository.findByUserIdAndPreferredNumber(anyString())).thenReturn(phone);
		final String phoneNumber = "31212";
		final UUID userId = UUID.randomUUID();
		// call
		final ResponseEntity<String> actual = classUnderTest.updateUsersPreferredPhoneNumber(userId, phoneNumber);

		// verify
		verify(mockPhoneRepository).findByUserIdAndPreferredNumber(userId.toString());
		final ResponseEntity<String> expected;
		if (phone.isPresent()) {
			expected = new ResponseEntity<String>(phoneNumber + " is updated for user " + userId, HttpStatus.OK);
			verify(mockPhoneRepository).save(phone.get());
		} else {
			expected = new ResponseEntity<String>("No preferred phone number found for " + userId,	HttpStatus.NOT_FOUND);
		}
		assertEquals(actual, expected);
		verifyNoMoreInteractions(mockPhoneRepository);
		verifyNoMoreInteractions(mockUserRepository);
	}

}