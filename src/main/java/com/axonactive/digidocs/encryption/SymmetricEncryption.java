package com.axonactive.digidocs.encryption;

public interface SymmetricEncryption {
	byte[] getIV();
	byte[] getSecretKeyBytes();
	byte[] encrypt(byte[] data) throws EncryptionException;
	byte[] decrypt(byte[] encryptedData) throws EncryptionException;
}
