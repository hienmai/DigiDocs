package com.axonactive.digidocs.utils;

import javax.enterprise.inject.spi.CDI;

public class CDIUtils {

	private CDIUtils() {
		//nothing
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return CDI.current().select(clazz).get();
	}
}
