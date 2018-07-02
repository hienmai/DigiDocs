package com.axonactive.digidocs.file;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.mockito.Mockito;

public class ScanCommandTest {
	
	@Test
	public void scanCommandExecuteFailTest() {
		ScanCommand scannedFile = new ScanCommand(new File("Not_Exist.pdf"));
		assertEquals(Result.FILE_NOT_EXIST, scannedFile.execute().getCommandResult().getResult());
	}
	
	@Test
	public void scanCommandExecuteSuccessTest() {
		File scannedFile = Mockito.mock(File.class);
		ScanCommand scanCommand = new ScanCommand(scannedFile);
		Mockito.when(scannedFile.exists()).thenReturn(true);
		assertEquals(Result.STEP_SUCESS, scanCommand.execute().getCommandResult().getResult());
	}
	
}
