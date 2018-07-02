package com.axonactive.digidocs.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.email.FileService;
import com.axonactive.digidocs.encryption.SymmetricEncryptionUtils;
import com.axonactive.digidocs.smb.SmbClient;
import com.axonactive.digidocs.utils.CDIUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CDIUtils.class, SymmetricEncryptionUtils.class})
public class MoveFileCommandTest {

	private ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private SmbClient smbClient = Mockito.mock(SmbClient.class);
	private FileService fileService = Mockito.mock(FileService.class);
	//private ClassLoader classLoader = MoveFileCommandTest.class.getClassLoader();
	private Command previousCommand = Mockito.mock(Command.class);
	private Command commandResult = Mockito.mock(Command.class);
	
	private File getFile(String fileName) {
		String url =
				Thread.currentThread().getContextClassLoader()
				.getResource(fileName).getFile();
		return new File(url);
	}
	
	@Before
	public void setUp() throws NoSuchAlgorithmException, NoSuchPaddingException {
		PowerMockito.mockStatic(CDIUtils.class);
		PowerMockito.when(CDIUtils.getBean(ConfigurationService.class))
					.thenReturn(configurationService);
		ConfigurationEntity configurationEntity = new ConfigurationEntity();
		ConfigurationBOM targetDirectoryBOM = Mockito.mock(ConfigurationBOM.class);
		PowerMockito.when(configurationService.findByKey("target.directory"))
					.thenReturn(Optional.of(configurationEntity));
		PowerMockito.when(configurationService.toBom(configurationEntity))
					.thenReturn(Optional.of(targetDirectoryBOM));
		PowerMockito.when(CDIUtils.getBean(SmbClient.class)).thenReturn(smbClient);
		PowerMockito.when(smbClient.writeFile(Mockito.anyString(), Mockito.any())).thenReturn(true);
		PowerMockito.when(CDIUtils.getBean(FileService.class)).thenReturn(fileService);
		PowerMockito.when(targetDirectoryBOM.getValue()).thenReturn("path");
		//PowerMockito.mockStatic(FileUtils.class);
		
	}

	
	@Test
	public void moveFileCommandFailEncryptTest() {
		File file = this.getFile("HCM_CEO_acceptable.pdf");
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		
		MoveFileCommand moveFileCommand = new MoveFileCommand(previousCommand, file);
		assertEquals(Result.FAIL_MOVING, moveFileCommand.execute().getCommandResult().getResult());
	}
	
	@Test
	public void moveFileCommandSuccessTest() {
		File file = this.getFile("HCM_CEO_acceptable.pdf");
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		
		PowerMockito.mockStatic(SymmetricEncryptionUtils.class);
		PowerMockito.when(SymmetricEncryptionUtils.encrypt(Mockito.any(), Mockito.any()))
					.thenReturn("AAA".getBytes());
		MoveFileCommand moveFileCommand = new MoveFileCommand(previousCommand, file);
		assertEquals(Result.STEP_SUCESS, moveFileCommand.execute().getCommandResult().getResult());
	}

	@Test
	public void moveFileCommandFailWithPreviousCommandFailTest() {
//		File file = this.getFile("HCM_CEO_acceptable.pdf");
		File file = Mockito.mock(File.class);
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.FILE_NOT_EXIST);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		
		MoveFileCommand moveFileCommand = new MoveFileCommand(previousCommand, file);
		assertEquals(Result.FILE_NOT_EXIST, moveFileCommand.execute().getCommandResult().getResult());
	}
	

}
