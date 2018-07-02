package com.axonactive.digidocs.utils;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class ApplicationResourceLoader {


	private PropertyFileResolver fileResolver;

	public ApplicationResourceLoader(String propertyFileName) {
		// TODO Auto-generated constructor stub
		fileResolver = new PropertyFileResolver(propertyFileName);
	}
	
	public String getPropertyAsString(String key) {

		String value = fileResolver.getProperty(key);

		if (StringUtils.isBlank(key) || Objects.isNull(value)) {

			throw new IllegalArgumentException("No property found with name " + key);

		}

		return value;
	}

}