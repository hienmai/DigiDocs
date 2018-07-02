package com.axonactive.digidocs.encryption;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import com.axonactive.digidocs.utils.FormatUtils;

public class SymmetricEncryptionFactory {
	private static final Logger LOGGER = Logger.getLogger(SymmetricEncryptionFactory.class);
	private static final String CHAR_SET_NAME = "UTF-8";
	private static final int KEY_LENGTH = 256;
	private static final int IV_LENGTH = 128;
	
	public static Optional<SymmetricEncryption> getAES256Encryption(String password, String salt) {
		String hashResult = BCrypt.hashpw(password, salt);
		byte[] hashResultByte;
		try {
			hashResultByte = hashResult.getBytes(CHAR_SET_NAME); // always 60
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Charset not supported " + CHAR_SET_NAME, e);
			return Optional.empty();
		}
		byte[] iv = Arrays.copyOf(hashResultByte, IV_LENGTH/8);
		byte[] secretKeyBytes = Arrays.copyOfRange(hashResultByte, IV_LENGTH/8 + 1, (IV_LENGTH + KEY_LENGTH)/8 + 1);
		return Optional.of(new AES256Encryption(secretKeyBytes, iv));
	}
	
	public static SymmetricEncryption getAES256EncryptionFromBase64(String keyBase64, String ivBase64) {
		return new AES256Encryption(FormatUtils.decodeFromBase64(keyBase64), FormatUtils.decodeFromBase64(ivBase64));
	}
}
