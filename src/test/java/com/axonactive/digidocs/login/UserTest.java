package com.axonactive.digidocs.login;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserTest {
	private User user = new User();
	
	@Test
	public void testGetAndSetAttributes() {
		user.setFirstName("Hai");
		user.setLastName("Nguyen Manh");
		
		assertEquals("Hai Nguyen Manh", user.getFullName());
	}

}
