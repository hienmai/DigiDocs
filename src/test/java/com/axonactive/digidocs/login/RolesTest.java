package com.axonactive.digidocs.login;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

@PrepareForTest({CDIUtils.class})
@RunWith(PowerMockRunner.class)
public class RolesTest {
	private User user = new User();
	
	@Test
	public void testGetRolePath() {
		ConfigurationService configurationService  = Mockito.mock(ConfigurationService.class);
		Mockito.when(configurationService.findByKey(Mockito.anyString())).thenReturn(Optional.of(new ConfigurationEntity()));
		ConfigurationBOM configurationBOM = new ConfigurationBOM();
		configurationBOM.setValue("Path To Storage");
		Mockito.when(configurationService.toBom(Mockito.any())).thenReturn(Optional.of(configurationBOM));
		PowerMockito.mockStatic(CDIUtils.class);
		PowerMockito.when(CDIUtils.getBean(Mockito.any())).thenReturn(configurationService);
		user.setRole( new ArrayList( Arrays.asList("CAO")));
		assertEquals(configurationBOM.getValue() + "/CAO", Roles.getRolePath(Roles.getRoles(user).get(0)));
	}

}
