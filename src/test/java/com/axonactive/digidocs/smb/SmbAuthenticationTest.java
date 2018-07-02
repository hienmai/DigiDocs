package com.axonactive.digidocs.smb;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

import jcifs.smb.NtlmPasswordAuthentication;

@RunWith(PowerMockRunner.class)
public class SmbAuthenticationTest {
	
	@Mock
	ConfigurationService configurationService;
	
	@InjectMocks
	SmbAuthentication smbAuthentication;

	// Test function "getAuth" with successful case
	@PrepareForTest({ CDIUtils.class })
	@Test
	public void testGetAuth() throws Exception {
		PowerMockito.mockStatic(CDIUtils.class);
		Optional<ConfigurationEntity> configurationEntity = Optional
				.of(PowerMockito.mock(ConfigurationEntity.class));
		PowerMockito.when(CDIUtils.getBean(ConfigurationService.class)).thenReturn(configurationService);
		PowerMockito.when(configurationService.findByKey(Mockito.anyString())).thenReturn(configurationEntity);
		PowerMockito.when(configurationService.toBom(Mockito.any())).thenReturn(Optional.of(new ConfigurationBOM()));
		Whitebox.invokeMethod(smbAuthentication, "init");
		NtlmPasswordAuthentication ntlmPasswordAuthentication = smbAuthentication.getAuth();
		Assert.assertNotNull(ntlmPasswordAuthentication);
	}

}
