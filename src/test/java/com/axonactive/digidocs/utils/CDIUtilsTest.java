package com.axonactive.digidocs.utils;

import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CDI.class})
public class CDIUtilsTest {

	@Test
	public void testGetBean() {
		PowerMockito.mockStatic(CDI.class);
		CDI<Object> cdiObject = Mockito.mock(CDI.class);
		Instance<Object> instance = Mockito.mock(Instance.class);
		Object object = Mockito.mock(Object.class);
		PowerMockito.when(CDI.current()).thenReturn(cdiObject);
		PowerMockito.when(cdiObject.select(Mockito.any(Class.class))).thenReturn(instance);
		PowerMockito.when(instance.get()).thenReturn(object);
		
		assertNotNull(CDIUtils.getBean(String.class));
	}
	
}
