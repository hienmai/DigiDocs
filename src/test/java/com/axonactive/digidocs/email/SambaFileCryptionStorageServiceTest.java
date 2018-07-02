package com.axonactive.digidocs.email;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionUtils;
import com.axonactive.digidocs.smb.SmbClient;



@PrepareForTest(SymmetricEncryptionUtils.class)
@RunWith(PowerMockRunner.class)
public class SambaFileCryptionStorageServiceTest {

	@Mock
	SmbClient smbClient;
	
	@InjectMocks
	SambaFileCryptionStorageService sambaFileCryptionStorageService;
	
	@Test
	public void readFileTest() {
		PowerMockito.mockStatic(SymmetricEncryptionUtils.class);
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		byte[] dataFile = "AAAA".getBytes();
		Mockito.when(smbClient.readFile(Mockito.anyString())).thenReturn(dataFile);
		PowerMockito.when(SymmetricEncryptionUtils.decrypt(dataFile, symmetricEncryption)).thenReturn(dataFile);
		
		assertNotNull(sambaFileCryptionStorageService.readFile(Mockito.anyString(), symmetricEncryption));
	}

	
	@Test
	public void writeFileTest() {
		PowerMockito.mockStatic(SymmetricEncryptionUtils.class);
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		byte[] dataFile = "AAAA".getBytes();
		Mockito.when(smbClient.writeFile(Mockito.anyString(), Mockito.any())).thenReturn(true);
		
		assertTrue(sambaFileCryptionStorageService.writeFile(dataFile, "", symmetricEncryption));
	}
}
