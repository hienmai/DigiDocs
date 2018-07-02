package com.axonactive.digidocs.encryption;

import static org.junit.Assert.assertNotEquals;

import java.security.InvalidAlgorithmParameterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.encryption.EncryptionException;
import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionUtils;

@RunWith(PowerMockRunner.class)
public class SymmetricCryptionUtilsTest {

	@Test
	public void testDecryptSuccess() throws EncryptionException {
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		Mockito.when(symmetricEncryption.decrypt(Mockito.any())).thenReturn(new byte[1]);
		assertNotEquals(0, SymmetricEncryptionUtils.decrypt(null, symmetricEncryption));
	}
	
	@Test
	public void testDecryptFail() throws EncryptionException {
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		Mockito.when(symmetricEncryption.decrypt(Mockito.any()))
				.thenThrow(new EncryptionException(new InvalidAlgorithmParameterException()));
		assertNotEquals(0, SymmetricEncryptionUtils.decrypt(null, symmetricEncryption));
	}
	
	@Test
	public void testEncryptSuccess() throws EncryptionException {
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		Mockito.when(symmetricEncryption.encrypt(Mockito.any())).thenReturn(new byte[1]);
		assertNotEquals(0, SymmetricEncryptionUtils.encrypt(null, symmetricEncryption));
	}
	
	@Test
	public void testEncryptFail() throws EncryptionException {
		SymmetricEncryption symmetricEncryption = Mockito.mock(SymmetricEncryption.class);
		Mockito.when(symmetricEncryption.encrypt(Mockito.any()))
				.thenThrow(new EncryptionException(new InvalidAlgorithmParameterException()));
		assertNotEquals(0, SymmetricEncryptionUtils.encrypt(null, symmetricEncryption));
	}
}
