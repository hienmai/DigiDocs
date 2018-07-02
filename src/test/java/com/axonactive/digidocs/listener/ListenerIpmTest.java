package com.axonactive.digidocs.listener;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.common.Branch;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.email.FileHandler;
import com.axonactive.digidocs.email.ServerConfiguration;
import com.axonactive.digidocs.login.RoleFactory;
import com.axonactive.digidocs.utils.CDIUtils;
import com.axonactive.digidocs.utils.FileUtils;

@PrepareForTest({ FileUtils.class, Branch.class, RoleFactory.class, CompletableFuture.class, FileHandler.class,
		CDIUtils.class, ServerConfiguration.class })
@RunWith(PowerMockRunner.class)
public class ListenerIpmTest {

	@Test
	public void testListenerToPathWithWrongBranch() throws Exception {
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.when(FileUtils.getBranch(Mockito.any())).thenReturn(Branch.NONE);
		FileChangeEvent fileChangeEvent = Mockito.mock(FileChangeEvent.class);
		FileObject fileObject = Mockito.mock(FileObject.class);
		Mockito.when(fileChangeEvent.getFile()).thenReturn(fileObject);
		FileName fileName = Mockito.mock(FileName.class);
		Mockito.when(fileName.toString()).thenReturn("file:AC_CEO_.pdf");
		Mockito.when(fileObject.getName()).thenReturn(fileName);
		new FileListenerIpm().fileCreated(fileChangeEvent);
	}

	@Test
	public void testListenerToPathWithWrongRole() throws Exception {
		FileChangeEvent fileChangeEvent = Mockito.mock(FileChangeEvent.class);
		FileObject fileObject = Mockito.mock(FileObject.class);
		Mockito.when(fileChangeEvent.getFile()).thenReturn(fileObject);
		FileName fileName = Mockito.mock(FileName.class);
		Mockito.when(fileName.toString()).thenReturn("file:HCM_CEO_.pdf");
		PowerMockito.mockStatic(FileUtils.class);
		Mockito.when(fileObject.getName()).thenReturn(fileName);
		PowerMockito.when(FileUtils.getBranch(Mockito.any())).thenReturn(Branch.CT);
		PowerMockito.when(FileUtils.getRecipientRole(Mockito.any())).thenReturn(Optional.of("wrongRole"));
		new FileListenerIpm().fileCreated(fileChangeEvent);
	}

	@Test
	public void fileCreated_failedException() throws Exception {
		FileChangeEvent fileChangeEvent = Mockito.mock(FileChangeEvent.class);
		FileObject fileObject = Mockito.mock(FileObject.class);
		Mockito.when(fileChangeEvent.getFile()).thenReturn(fileObject);
		FileName fileName = Mockito.mock(FileName.class);
		Mockito.when(fileName.toString()).thenReturn("file:HCM_CEO_.pdf");
		PowerMockito.mockStatic(FileUtils.class);
		Mockito.when(fileObject.getName()).thenReturn(fileName);
		PowerMockito.when(FileUtils.getBranch(Mockito.any())).thenReturn(Branch.CT);
		PowerMockito.when(FileUtils.getRecipientRole(Mockito.any())).thenReturn(Optional.of("wrongRole"));
		PowerMockito.mockStatic(RoleFactory.class);
		PowerMockito.when(RoleFactory.checkAvailableRole(Mockito.anyString())).thenReturn(true);
		PowerMockito.mockStatic(FileHandler.class);
		PowerMockito.mockStatic(ServerConfiguration.class);
		ServerConfiguration serverConfiguration = new ServerConfiguration("anyString");
		PowerMockito.when(ServerConfiguration.getInstance()).thenReturn(serverConfiguration);
		PowerMockito.mockStatic(CDIUtils.class);
		Mockito.mock(ConfigurationService.class);
		ConfigurationService configurationService = new ConfigurationService();
		PowerMockito.when(CDIUtils.getBean(ConfigurationService.class)).thenReturn(configurationService);
		new FileListenerIpm().fileCreated(fileChangeEvent);
	}

}