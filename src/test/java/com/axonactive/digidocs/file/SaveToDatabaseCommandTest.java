package com.axonactive.digidocs.file;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.email.FileService;
import com.axonactive.digidocs.utils.CDIUtils;

@RunWith(PowerMockRunner.class)
public class SaveToDatabaseCommandTest {

	private FileService fileService = Mockito.mock(FileService.class);
	
	private Command previousCommand = Mockito.mock(Command.class); 
	private Command commandResult = Mockito.mock(Command.class);
	
	@Before
	@PrepareForTest({CDIUtils.class})
	public void setUp() {
		PowerMockito.mockStatic(CDIUtils.class);
		PowerMockito.when(CDIUtils.getBean(FileService.class))
					.thenReturn(fileService);
	}
	
	@Test
	@PrepareForTest({CDIUtils.class})
	public void testSaveToDbSuccess() {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		FileEntity fileEntity = Mockito.mock(FileEntity.class);
		Mockito.when(fileService.save(Mockito.any())).thenReturn(fileEntity);
		Mockito.when(fileService.toEntity(Mockito.any())).thenReturn(Optional.of(fileEntity));
		
		Command command = new SaveToDatabaseCommand(previousCommand);
		assertEquals(Result.STEP_SUCESS, command.execute().getCommandResult().getResult());
	}
	
	@Test
	@PrepareForTest({CDIUtils.class})
	public void testSaveToDbWithPreviousCommandFailSuccess() {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.FAIL_MOVING);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		Command command = new SaveToDatabaseCommand(previousCommand);
		assertEquals(Result.FAIL_MOVING, command.execute().getCommandResult().getResult());
	}
	
	@Test
	@PrepareForTest({CDIUtils.class})
	public void testSaveToDbFail() {
		Mockito.when(previousCommand.execute()).thenReturn(commandResult);
		CommandResult commandResultResult = new CommandResult();
		commandResultResult.setResult(Result.STEP_SUCESS);
		Mockito.when(commandResult.getCommandResult()).thenReturn(commandResultResult);
		FileEntity fileEntity = Mockito.mock(FileEntity.class);
		Mockito.when(fileService.save(Mockito.any())).thenReturn(fileEntity);
		Mockito.when(fileService.toEntity(Mockito.any())).thenReturn(Optional.empty());
		
		Command command = new SaveToDatabaseCommand(previousCommand);
		assertEquals(Result.FAIL_DATABASE_SAVING, command.execute().getCommandResult().getResult());
	}
}
