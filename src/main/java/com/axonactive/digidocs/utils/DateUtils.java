package com.axonactive.digidocs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	private DateUtils() {
	}

	public static String getDateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				ConfigPropertiesFileUtils.getValue("file.name.format").get());
		return dateFormat.format(date);
	}

}
