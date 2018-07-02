package com.axonactive.digidocs.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.log4j.Logger;

public class FormatUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FormatUtils.class);
	private FormatUtils() {
		
	}

	public static String toBase64(String source) {
		if(source == null) {
			return "";
		}
		try {
			return Base64.getEncoder().encodeToString(source.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Unsupported Charset", e);
			return "";
		}
	}
	
	public static  String[] toBase64(String[] source) {
		if(source == null) {
			return new String[0];
		}
		String[] result = new String[source.length];
		for(int i = 0; i < source.length; i++) {
			result[i] = toBase64(source[i]);
		}
		return result;
	}
	
	public static String toBase64(byte[] source) {
		return Base64.getEncoder().encodeToString(source);
	}
	
	public static byte[] decodeFromBase64(String sourceBase64) {
		return Base64.getDecoder().decode(sourceBase64);
	}
}
