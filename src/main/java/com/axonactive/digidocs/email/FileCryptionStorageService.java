package com.axonactive.digidocs.email;

import com.axonactive.digidocs.encryption.SymmetricEncryption;

public interface FileCryptionStorageService {
	
	
	/**
	 * Read file and decrypt the data
	 * @param path
	 * @param symmetricEncryption
	 * @return byte[]
	 */
	byte[] readFile(String path, SymmetricEncryption symmetricEncryption);
	
	/**
	 * Write file and encrypt the data
	 * @param path
	 * @param symmetricEncryption
	 * @return byte[]
	 */
	boolean writeFile(byte[] data, String path, SymmetricEncryption symmetricEncryption);
}
