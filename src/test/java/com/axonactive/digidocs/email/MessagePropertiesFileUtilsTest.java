package com.axonactive.digidocs.email;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.axonactive.digidocs.utils.MessagePropertiesFileUtils;

public class MessagePropertiesFileUtilsTest {
	
	@Test
	public void testGetValueSuccess() {
		Assert.assertEquals("get Value", "Mail from {0} Branch", MessagePropertiesFileUtils.getValue("emailSubject").orElse(""));
	}
	
	@Test
	public void testGetValueWhenKeyNotExist() {
		assertEquals(Optional.empty(), MessagePropertiesFileUtils.getValue("Wrong.key"));
	}
	
	@Test
	public void testGetValueFailWithNullKey() {
		Assert.assertEquals("get Value", Optional.empty(), MessagePropertiesFileUtils.getValue(null));
	}
	
	

}
