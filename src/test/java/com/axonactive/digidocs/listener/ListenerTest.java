package com.axonactive.digidocs.listener;

import java.util.Optional;

import javax.servlet.ServletContextEvent;

import org.apache.commons.vfs2.FileSystemException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

@PrepareForTest({ CDIUtils.class })
@RunWith(PowerMockRunner.class)
public class ListenerTest {

	static ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

	@BeforeClass
	public static void init() {
		PowerMockito.mockStatic(CDIUtils.class);
		PowerMockito.when(CDIUtils.getBean(ConfigurationService.class)).thenReturn(configurationService);
	}

	@Test
	public void testListentoPathWithNullBom() throws FileSystemException {
		PowerMockito.when(configurationService.findByKey(Mockito.anyString()))
				.thenReturn(Optional.ofNullable(new ConfigurationEntity()));
		PowerMockito.when(configurationService.toBom(Mockito.any()))
				.thenReturn(Optional.empty());
		Listener.listenToPath("abc");
	}

	@Rule
	TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testListentoPath() throws Exception {
		ConfigurationBOM configurationBOM = new ConfigurationBOM(1, "add", temporaryFolder.getRoot().getAbsolutePath());
		PowerMockito.when(configurationService.findByKey(Mockito.anyString()))
				.thenReturn(Optional.ofNullable(new ConfigurationEntity()));
		PowerMockito.when(configurationService.toBom(Mockito.any())).thenReturn(Optional.of(configurationBOM));

		Listener listener = new Listener();
		ServletContextEvent servletContextEvent = Mockito.mock(ServletContextEvent.class);
		listener.contextInitialized(servletContextEvent);

		listener.contextDestroyed(servletContextEvent);

	}
	
	
}
