package com.axonactive.digidocs.encryption;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Encryption implements SymmetricEncryption {

	private static final String CRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final int KEY_LENGTH = 256;
	private static final int IV_LENGTH = 128;
	private byte[] ivBytes;
	private SecretKey secretKey;

	public AES256Encryption() {
		this.secretKey = getRandomSecretKey();
		this.ivBytes = generateIV();
	}

	public AES256Encryption(byte[] secretKeyBytes, byte[] iv) {
		this.secretKey = new SecretKeySpec(secretKeyBytes, CRYPTION_ALGORITHM.split("/")[0]);
		this.ivBytes = iv;
	}

	private SecretKey getRandomSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[KEY_LENGTH / 8];
		random.nextBytes(bytes);
		return new SecretKeySpec(bytes, CRYPTION_ALGORITHM.split("/")[0]);
	}

	private byte[] generateIV() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[IV_LENGTH / 8];
		random.nextBytes(bytes);
		return bytes;
	}

	@Override
	public byte[] getIV() {
		return ivBytes;
	}

	@Override
	public byte[] getSecretKeyBytes() {
		return this.secretKey.getEncoded();
	}

	@Override
	public byte[] decrypt(byte[] encryptedData) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(CRYPTION_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, this.secretKey, new IvParameterSpec(ivBytes));
			return cipher.doFinal(encryptedData);
		} catch (Exception e) {
			throw new EncryptionException(e);
		}
	}

	@Override
	public byte[] encrypt(byte[] data) throws EncryptionException {
		try {
			Cipher cipher = Cipher.getInstance(CRYPTION_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, this.secretKey, new IvParameterSpec(this.ivBytes));
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new EncryptionException(e);
		}
	}

}
