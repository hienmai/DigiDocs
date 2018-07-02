package com.axonactive.digidocs.utils;

import java.util.Optional;

import org.apache.log4j.Logger;

public class ConfigPropertiesFileUtils {

	private ConfigPropertiesFileUtils() {
	}
	
	private static final Logger LOGGER = Logger.getLogger(ConfigPropertiesFileUtils.class);
	private static ApplicationResourceLoader applicationResourceLoader;
	
	static {
		applicationResourceLoader = new ApplicationResourceLoader("config.properties");
	}

	public static Optional<String> getValue(String key) {
		if (applicationResourceLoader == null || key == null) {
			return Optional.empty();
		}
		try{
			return Optional.of(applicationResourceLoader.getPropertyAsString(key));
		}catch(Exception e) {
			LOGGER.error("Get the key failed", e);
			return Optional.empty();
		}
	}
	
}
