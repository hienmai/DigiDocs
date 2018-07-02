package com.axonactive.digidocs.utils;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertyFileResolver {

	private Map<String, String> properties = new HashMap<>();
	private static final Logger LOGGER = Logger.getLogger(PropertyFileResolver.class);
	
	public PropertyFileResolver(String propertyFileName) {

		InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(propertyFileName);

		Properties applicationProperties = new Properties();

		try {
			applicationProperties.load(inputStream);
		} catch (Exception e) {
			LOGGER.error("Unable to load properties file", e);
		}

		applicationProperties.forEach((key, value) -> properties.put(key.toString(), value.toString()));
		
	}

	public String getProperty(String key) {

		return properties.get(key);

	}

}
