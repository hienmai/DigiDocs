package com.axonactive.digidocs.encryption;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.utils.FormatUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BCrypt.class, FormatUtils.class})
public class SymmetricEncryptionFactoryTest {
	
	@Test
	public void testGetAES256EncryptionSuccess() throws UnsupportedEncodingException{
		assertTrue(SymmetricEncryptionFactory.getAES256Encryption("Some Random Password", "$2a$06$qVxS016NiF4nfENsbvNzqe").isPresent());
	}
	
	@Test
	public void testGetAES256EncryptionFromBase64() {
		PowerMockito.mockStatic(FormatUtils.class);
		FileBOM fileBOM = Mockito.mock(FileBOM.class);
		PowerMockito.when(FormatUtils.decodeFromBase64(Mockito.anyString())).thenReturn("AAA".getBytes());
		assertNotNull(SymmetricEncryptionFactory.getAES256EncryptionFromBase64(fileBOM.getKeyBase64(), fileBOM.getIvBase64()));
	}
}
