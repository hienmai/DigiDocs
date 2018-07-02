package com.axonactive.digidocs.encryption;

import org.apache.log4j.Logger;

public class SymmetricEncryptionUtils {
	private SymmetricEncryptionUtils() {
		//nothing
	}

	private static final Logger LOGGER = Logger.getLogger(SymmetricEncryptionUtils.class);
	
	public static byte[] decrypt(byte[] encryptedData, SymmetricEncryption symmetricEncryption) {
		byte[] plainData;
		try {
			plainData = symmetricEncryption.decrypt(encryptedData);
		} catch (EncryptionException e) {
			LOGGER.error("Can't decrypt the data", e);
			return new byte[0];
		}
		return plainData;
	}
	
	public static byte[] encrypt(byte[] plainData, SymmetricEncryption symmetricEncryption) {
		try {
			return symmetricEncryption.encrypt(plainData);
		} catch (EncryptionException e) {
			LOGGER.error("Can't encrypt the data", e);
			return new byte[0];
		}
	}
}
