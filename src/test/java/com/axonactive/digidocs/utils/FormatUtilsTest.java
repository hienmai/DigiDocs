package com.axonactive.digidocs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FormatUtilsTest {

	@Test
	public void shouldReturnEmptyStringWhenTheSourceIsNull() {
		String base64 = FormatUtils.toBase64((String)null);
		assertEquals("", base64);
	}
	
	@Test
	public void shouldReturnACorrectValueWhenTheSourceIsNotNull() {
		String base64 = FormatUtils.toBase64("ho chi minh");
		assertEquals(base64, "aG8gY2hpIG1pbmg=");
	}
	
	@Test
	public void shouldReturnEmptyArrayWhenTheSourceIsNull() {
		String[] base64 = FormatUtils.toBase64((String[])null);
		assertEquals(base64.length, 0);
	}
	
	@Test
	public void shouldReturnACorrectValueArrayWhenTheSourceIsNotNull() {
		String[] source =  new String[] {"ho chi minh", "da nang"};
		String[] base64 = FormatUtils.toBase64(source);
		assertEquals(base64[0], "aG8gY2hpIG1pbmg=");
		assertEquals(base64[1], "ZGEgbmFuZw==");
	}
	
	@Test
	public void shouldConvertBytesToStringSuccessfully() {
		byte[] source = "ABC DEF".getBytes();
		System.out.println(FormatUtils.toBase64(source));
		assertNotNull(FormatUtils.toBase64(source));
	}
	
	@Test
	public void shouldDecodeStringToBytesSuccessfully() {
		String source = "QUJDIERFRg==";
		assertNotNull(new String(FormatUtils.decodeFromBase64(source)));
	}
}
