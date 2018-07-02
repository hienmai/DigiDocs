package com.axonactive.digidocs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateUtilsTest {
	
	private Date date;
	
	@Before
	public void setUp() {
		date = new Date();
	}
	
	@Test
	public void testGetDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		String expectedFormattedDate = dateFormat.format(date);
		String formattedDate = DateUtils.getDateFormat(date);
		Assert.assertEquals(expectedFormattedDate, formattedDate);
	}
	
}
