package com.axonactive.digidocs.utils;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class ConfigPropertiesFileUtilsTest {
	
	@Test
	public void testGetEmptyValue() {
		Optional<String> value = ConfigPropertiesFileUtils.getValue(null);
		Assert.assertFalse(value.isPresent());
	}
	
	@Test
	public void testGetValue() {
		String hostValue = ConfigPropertiesFileUtils.getValue("file.extension").get();
		String expectedHostValue = ".pdf";
		Assert.assertEquals(expectedHostValue, hostValue);
	}
	
	@Test
	public void testGetNotExitstValue() {
		Optional<String> value = ConfigPropertiesFileUtils.getValue("mail");
		Assert.assertFalse(value.isPresent());
	}
	
}
