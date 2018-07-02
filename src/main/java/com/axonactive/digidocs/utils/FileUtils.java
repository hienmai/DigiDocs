package com.axonactive.digidocs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.axonactive.digidocs.common.Branch;

public class FileUtils {

	private FileUtils() {
	}

	private static final int BRANCH_INDEX = 0;
	private static final int RECIPIENT_INDEX = 1;
	private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

	/**
	 * Get Branch's name through filename
	 * @param file
	 * @return branch
	 */
	public static Branch getBranch(File file) {
		String fileName = file.getName();

		String branchKey = fileName.split("_")[BRANCH_INDEX];

		for (Branch branch : Branch.values()) {
			if (branch.name().equals(branchKey))
				return branch;
		}
		return Branch.NONE;

	}

	/**
	 * Get the receiver's role
	 * @param file
	 * @return subsets
	 */
	public static Optional<String> getRecipientRole(File file) {
		String fileName = file.getName();
		String[] subsets = fileName.split("_");
		if (subsets.length >= 2) {
			return Optional.of(subsets[RECIPIENT_INDEX]);
		}
		return Optional.empty();
	}

	/**
	 * Convert file size
	 * 
	 * @param file
	 * @return string
	 * @author dhphuc
	 */
	public static String convertFileSize(long size) {
		long fileSize = size / 1024;
		if (fileSize > 1024)
			return (fileSize / 1024) + " MB";
		return fileSize + " KB";
	}
	
	/**
	 * Read file and convert to byte array
	 * @param file
	 * @return byte[]
	 */
	public static byte[] getAllData(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] data = IOUtils.toByteArray(fileInputStream);
			fileInputStream.close();
			return data;
		} catch (IOException e) {
			LOGGER.error("Cant read file " + file.getAbsolutePath(), e);
			return new byte[0];
		}
	}

}
