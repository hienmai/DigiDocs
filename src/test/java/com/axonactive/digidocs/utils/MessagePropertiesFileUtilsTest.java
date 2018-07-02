package com.axonactive.digidocs.utils;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class MessagePropertiesFileUtilsTest {

	@Test
	public void testGetEmptyValue() {
		Optional<String> value = MessagePropertiesFileUtils.getValue(null);
		Assert.assertFalse(value.isPresent());
	}
	@Test
	public void testGetValue() {
		String hostValue = MessagePropertiesFileUtils.getValue("loginFailed.message").get();
		String expectedHostValue = "Invalid username or password.";
		Assert.assertEquals(expectedHostValue, hostValue);
	}
	
	@Test
	public void testGetNotExitstValue() {
		Optional<String> value = MessagePropertiesFileUtils.getValue("loginTimeOut.message");
		Assert.assertFalse(value.isPresent());
	}
	
}
