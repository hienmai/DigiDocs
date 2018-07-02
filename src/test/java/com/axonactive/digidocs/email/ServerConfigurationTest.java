package com.axonactive.digidocs.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.utils.ConfigPropertiesFileUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigPropertiesFileUtils.class })
@PowerMockIgnore({ "javax.net.ssl.*" })
public class ServerConfigurationTest {

	ServerConfiguration serverConfiguration;
	
	@Test
	public void testGetInstance_IfTokenNotPresent()
	{
		PowerMockito.mockStatic(ConfigPropertiesFileUtils.class);
		PowerMockito.when(ConfigPropertiesFileUtils.getValue("mail.api.url.sendMail")).thenReturn(Optional.of("abc"));
		PowerMockito.when(ConfigPropertiesFileUtils.getValue("mail.api.url.getTokenKey")).thenReturn(Optional.empty());
		ServerConfiguration result = ServerConfiguration.getInstance();
		assertNotNull(result);
	}

	@Test
	public void testGetInstance_IfURLNotPresent() {
		PowerMockito.mockStatic(ConfigPropertiesFileUtils.class);
		PowerMockito.when(ConfigPropertiesFileUtils.getValue(Mockito.anyString())).thenReturn(Optional.empty());
		ServerConfiguration result = ServerConfiguration.getInstance();
		assertNull(result.getTokenKey());
	}

	@Test
	public void testGetInstance() {
		serverConfiguration = ServerConfiguration.getInstance();
		assertNotNull(serverConfiguration);
	}

	@Test
	public void testRefreshTokenKey() {
		serverConfiguration = ServerConfiguration.getInstance();
		boolean result = serverConfiguration.refreshTokenKey();
		assertTrue(result);
	}

	@Test
	public void testRefreshTokenKey_IfUrlNotPresent() {
		serverConfiguration = ServerConfiguration.getInstance();
		PowerMockito.mockStatic(ConfigPropertiesFileUtils.class);
		PowerMockito.when(ConfigPropertiesFileUtils.getValue(Mockito.anyString())).thenReturn(Optional.empty());
		boolean result = serverConfiguration.refreshTokenKey();
		assertFalse(result);
	}

	@Test
	public void testRefreshTokenKey_IfCannotExecute() {
		serverConfiguration = ServerConfiguration.getInstance();
		PowerMockito.mockStatic(ConfigPropertiesFileUtils.class);
		PowerMockito.when(ConfigPropertiesFileUtils.getValue(Mockito.anyString())).thenReturn(Optional.of("abc"));
		boolean result = serverConfiguration.refreshTokenKey();
		assertFalse(result);
	}

	@After
	public void destory()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (serverConfiguration == null) {
			return;
		}
		Class c = serverConfiguration.getClass();
		Field instanceField = c.getDeclaredField("instance");
		instanceField.setAccessible(true);
		instanceField.set(this, null);
	}
	
}
