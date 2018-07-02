package com.axonactive.digidocs.file;


import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import lombok.Getter;

@Getter
public class ScanCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(ScanCommand.class);
	
	private static final int MAXIMUM_WAITING_TIME = 20000; // mili-seconds

	private CommandResult commandResult = new CommandResult();	
	private File scannedFile;
	
	public ScanCommand(File file) {
		this.scannedFile = file;
	}
	
	private void waitingForACompleteFile() {
		Date date = new Date();
		long fileSize = this.scannedFile.length();
		while (new Date().getTime() - date.getTime() < MAXIMUM_WAITING_TIME) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				LOGGER.error("Interrupted when sleeping", e);
			}
			long newFileSize = this.scannedFile.length();
			if (newFileSize != fileSize) {
				fileSize = newFileSize;
				date = new Date();
			}
		}
		LOGGER.debug(scannedFile.getName() + " is complete.");
	}
	
	@Override
	public Command execute() {
		LOGGER.debug("Handling file : " + scannedFile.getName());
		waitingForACompleteFile();
		if (!scannedFile.exists()) {
			LOGGER.debug("File no longer exists: " + scannedFile.getName());
			this.commandResult.setResult(Result.FILE_NOT_EXIST);
		} else {
			this.commandResult.setResult(Result.STEP_SUCESS);
		}
		
		return this;
	}
}