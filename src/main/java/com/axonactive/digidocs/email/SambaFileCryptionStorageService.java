package com.axonactive.digidocs.email;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionUtils;
import com.axonactive.digidocs.smb.SmbClient;

@ApplicationScoped
public class SambaFileCryptionStorageService implements FileCryptionStorageService {

	@Inject
	private SmbClient smbClient;

	@Override
	public byte[] readFile(String path, SymmetricEncryption symmetricEncryption) {
		byte[] dataFile = smbClient.readFile(path);
		return SymmetricEncryptionUtils.decrypt(dataFile, symmetricEncryption);
	}

	@Override
	public boolean writeFile(byte[] data, String path, SymmetricEncryption symmetricEncryption) {
		byte[] encryptedData =  SymmetricEncryptionUtils.encrypt(data, symmetricEncryption);
		return smbClient.writeFile(path, encryptedData);
	}
}
