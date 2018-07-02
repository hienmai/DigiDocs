package com.axonactive.digidocs.utils;

import java.util.Optional;

import org.apache.log4j.Logger;

public class MessagePropertiesFileUtils {

	private MessagePropertiesFileUtils() {
	}
	
	private static ApplicationResourceLoader applicationResourceLoader;
	private static final Logger LOGGER = Logger.getLogger(MessagePropertiesFileUtils.class);
	
	static {
		applicationResourceLoader = new ApplicationResourceLoader("message.properties");
	}

	public static Optional<String> getValue(String key) {
		if (applicationResourceLoader == null || key == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(applicationResourceLoader.getPropertyAsString(key));
		}catch(Exception e){
			LOGGER.error("Get the key failed " + key, e);
			return Optional.empty();
		}
	}

}
