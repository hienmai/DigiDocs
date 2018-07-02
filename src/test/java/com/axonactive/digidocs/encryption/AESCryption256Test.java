package com.axonactive.digidocs.encryption;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Ignore;
import org.junit.Test;

import com.axonactive.digidocs.utils.FormatUtils;

public class AESCryption256Test {
	
	@Test
	public void shouldEncryptAndDecryptSuccessfully() throws Exception {
		SymmetricEncryption symmetricEncryption = new AES256Encryption();
		byte[] plainData = "Hello world".getBytes("UTF-8");
		byte[] cipherData = symmetricEncryption.encrypt(plainData);
		assertThat(plainData, IsNot.not(IsEqual.equalTo(cipherData)));
		
		byte[] secretKeyBytes = symmetricEncryption.getSecretKeyBytes();
		byte[] iv = symmetricEncryption.getIV();
		
		SymmetricEncryption symmetricCryption2 = new AES256Encryption(secretKeyBytes, iv);
		
		byte[] plainData2 = symmetricCryption2.decrypt(cipherData);
		assertArrayEquals(plainData, plainData2);
	}
	
	@Ignore
	@Test(expected = EncryptionException.class)
	public void test() throws EncryptionException {
		SymmetricEncryption SYMMETRIC_CRYPTION = SymmetricEncryptionFactory.getAES256Encryption("Some Random Password", "$2a$06$qVxS016NiF4nfENsbvNzqe").get();
		byte[] value = SYMMETRIC_CRYPTION.encrypt("admin123!@#".getBytes(Charset.forName("UTF-8")));
		System.out.println(FormatUtils.toBase64(value));
	}
	
	@Test(expected = EncryptionException.class)
	public void testEncryptionException() throws EncryptionException {
		SymmetricEncryption SYMMETRIC_CRYPTION = new AES256Encryption("kdf".getBytes(), "fff".getBytes());
		byte[] value = SYMMETRIC_CRYPTION.encrypt("admin123!@#".getBytes(Charset.forName("UTF-8")));
	}
	
	@Test(expected = EncryptionException.class)
	public void testDecryptionException() throws EncryptionException {
		SymmetricEncryption SYMMETRIC_CRYPTION = new AES256Encryption("kdf".getBytes(), "fff".getBytes());
		byte[] value1 = SYMMETRIC_CRYPTION.decrypt("sth".getBytes());
	}
	
	
}
